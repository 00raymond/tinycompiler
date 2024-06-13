import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import result.Result;

public class Parser {
    private static int currTk;
    private static boolean isInWhileBody = false;
    private static HashMap<String, Integer> varTable = new HashMap<>();
    private static String tkStr;
    private static ArrayList<Object> tempTerms = new ArrayList<>();
    private static int nestedLoop = 0;

    public static int getLoopDepth() {
        return nestedLoop;
    }

    private static void setIsInWhileBody(boolean value) {
        isInWhileBody = value;
    }

    private static boolean getIsInWhileBody() {
        return isInWhileBody;
    }

    public static void next() {
        currTk = Tokenizer.getNext();
        tkStr = Tokenizer.Id2String(currTk);
    }

    public static Result number() {
        tempTerms.add(Tokenizer.getNumber());
        return new Result(0, (Tokenizer.getNumber()));
    }

    public static Result identifier() {
        tempTerms.add(tkStr);
        // value will be the index of the identifier in the varTable
        return new Result(1, Tokenizer.String2Id(tkStr));
    }

    public static Result relation() throws Exception { //outputs 1 if condition is true, 0 if false
        tempTerms = new ArrayList<>();
        Result x = expression();
        if (tempTerms.size() == 1 && tempTerms.get(0) instanceof Integer) {
            int term = (int) tempTerms.get(0);
            Result x1 = new Result(0, term);
            x1.setInstructionSp(SSA.addConst(term));
            tempTerms.clear();
            x = x1;
        } else if (tempTerms.size() == 1 && (tempTerms.get(0) instanceof Character || tempTerms.get(0) instanceof String)) {
            String variable = (String) tempTerms.get(0);
            Result x1 = new Result();
            x1.setInstructionSp(SSA.findVariableSkipElseThenBody(variable));
            tempTerms.clear();
            x = x1;
        }

        String relOp = tkStr;
        next(); // now tokenizer consumes relOp

        Result y = expression(); // y will be the result of the second expression
        if (tempTerms.size() == 1) {
            if (tempTerms.getFirst() instanceof Integer) {
                int term = (int) tempTerms.getFirst();
                Result y1 = new Result(0, term);
                y1.setInstructionSp(SSA.addConst(term));
                tempTerms.clear();
                y = y1;
            } else if ((tempTerms.getFirst() instanceof Character || tempTerms.getFirst() instanceof String)) {
                String variable = (String) tempTerms.getFirst();
                Result y1 = new Result();
                y1.setInstructionSp(SSA.findVariableSkipElseThenBody(variable));
                tempTerms.clear();
                y = y1;
            }
        }

        String branchType = getBranchType(relOp);
        SSA.addBranch(branchType, SSA.addCmp(x, y));
        // br + cmp + new instruct (empty)

        boolean isTrue = compare(x, y, relOp);

        return new Result(5, isTrue ? 1 : 0);
    }



    public static void whileLoop() throws Exception {

        next();
        // consume "while"
        // example while x<10 do <statsequence> od

        SSA.createBasicBlock();
        SSA.changeCurrBbWhile(); // now indicated via the variable isWhile in the basic block that its a while header
        int whileHeaderPosition = SSA.getBbp();
        SSA.createBasicBlock(); // create a separate basic block for the cmp/branch instructions.
        SSA.changeCurrBbWhileRel();
        int whileRelPosition = SSA.getBbp();

        // must create phi functions somewhere here or before, since we need to know which variables have been changed in the while loop body.
        // we will use the new lookahead method to determine which variables have been changed in the while loop body.
        int i = 0;
        String temp = tkStr;
        String currentLookAhead = Tokenizer.lookAhead(i);
        ArrayList<String> addedTemp = new ArrayList<>();
        String oldLine = currentLookAhead;
        int nest = 0;
        while (!currentLookAhead.contains("od")) {
            if (currentLookAhead.contains("do")) {
                // skip to after the do line
                oldLine = currentLookAhead;
                currentLookAhead = currentLookAhead.substring(currentLookAhead.indexOf("do") + 2);
                nest++;
            }

            if (oldLine.contains("while") && nest > 1) {
                System.out.println("i is currently: " + i + " and the lookahead is: " + currentLookAhead);

                while (!currentLookAhead.contains("od")) {

                    if (currentLookAhead.contains("let")) {

                        String[] split = currentLookAhead.trim().split("\\s+");
                        String varName = split[1];
                        System.out.println("asdf:" + varName);

                        if (addedTemp.contains(varName)) {
                            i++;
                            oldLine = currentLookAhead;
                            currentLookAhead = Tokenizer.lookAhead(i);
                            continue;
                        }
                        oldLine = currentLookAhead;
                        addedTemp.add(varName);
                        SSA.generatePhiFromLookAhead(varName, whileHeaderPosition);

                    }

                    i++;
                    oldLine = currentLookAhead;
                    currentLookAhead = Tokenizer.lookAhead(i);
                }
            }

            // if its a let statement, we need to create the phi function
            if (currentLookAhead.contains("let")) {
                String[] split = currentLookAhead.trim().split("\\s+");
                String varName = split[1];

                if (addedTemp.contains(varName)) {
                    i++;
                    currentLookAhead = Tokenizer.lookAhead(i);
                    continue;
                }
                addedTemp.add(varName);
                System.out.println(varName);

                SSA.generatePhiFromLookAhead(varName, whileHeaderPosition);

            }
            System.out.println(Tokenizer.lookAhead(i));
            i++;
            currentLookAhead = Tokenizer.lookAhead(i);
        }
        i = 0;

        // indicate that this is the header somehow in basic block code

        Result condition = relation();

        // we will fill in the while loop basic block AFTER the while body, so we know what variables need to be added to the header.
        // iterate through each bbp until reached whileHeaderPosition, and if the block is a then or else block skip it. when
        // adding variables and their instructions, if the variable has been added DO NOT ADD it, since the most recent change to the
        // variable should be kept.

        next(); // consume "do"
        // now at the start of the statsequence
        SSA.createBasicBlock(); // create basic block for the body

        statSequence();

        if (tkStr.equals("od")) {

            next(); // consume od

            // NO NEED FOR EMPTY INSTRUCTION! JUST POINT TO FIRST INSTRUCTION IN THE BASIC BLOCK.
            // HERE: add phi functions for variables that have been changed in the while loop body
//            SSA.addPhiWhile(whileHeaderPosition, whileRelPosition);
            SSA.addBra(whileHeaderPosition); // add branch instruction to the while loop header

            // create else block

            SSA.createBasicBlock();
            // keep in mind that we will need to identify the continue block. how can we identify which block is the else of the while loop?
            // lets just set a variable stating its the continue block.
            SSA.changeCurrBbWhileContinue(); // now when generating graph code, we can go from while header -> while continue on one side, and while header -> while body on the other side.

            // solve problem of ordering: SEPARATE the basic blocks for the phi functions in the while loop header.
            next();
        }

    }

    public static void ifStatement() throws Exception {
        next(); // consume "if"
        Result condition = relation();
        next(); // consume "then"

        SSA.createBasicBlock();
        SSA.changeCurrBbThen();
        // bbp pos now +1, SSA changes in the following statSequence will apply to a new basic block
        statSequence();
        if (tkStr.equals("else")) {
            next(); // consume else

            // bbp pos now +1, SSA changes in the following statSequence will apply to a new basic block
            SSA.createBasicBlock();
            SSA.changeCurrBbElse();
            statSequence(); // proceed with else statsequence
            // add instruction ( bra (current sp) ) and add current sp empty instruction to the join block
            SSA.addBra();
        }

        if (tkStr.equals("fi")) {
            next(); // consume fi
        }

        SSA.generatePhi();
    }

    private static String getBranchType(String relOp) {
        return switch (relOp) {
            case "<" -> "blt";
            case ">" -> "bgt";
            case "==" -> "beq";
            case "!=" -> "bne";
            case "<=" -> "ble";
            case ">=" -> "bge";
            default -> throw new IllegalArgumentException("Unsupported relational operator: " + relOp);
        };
    }

    public static Result factor() throws Exception {

        Result x;

        if (varTable.containsKey(tkStr)) { // need to check if the tkStr exists in the varTable. then use the identifiers numerical value
            x = identifier();
            next();
        } else if (onlyDigits(tkStr)) {
            x = number();
            next();
        } else if (tkStr.equals("(")) {
            next();
            x = expression();

            if (!tkStr.equals(")")) {
                syntaxError();
            } else {
                next();
            }
        } else if (isRelOp(tkStr)) {
            return new Result(3, Tokenizer.String2Id(tkStr)); // Return immediately for relational operators
        } else {
            syntaxError();
            return new Result(4);
        }
        return x;
    }

    public static Result term() throws Exception {
        Result x, y;

        x = factor();

        while (tkStr.equals("*") | tkStr.equals("/")) {

            int op = Tokenizer.String2Id(tkStr);

            next();
            y = factor();
            x = Register.Compute(op, x, y);
            x.setKind(6);
        }
        return x;
    }

    public static Result expression() throws Exception {

        Result x, y;
        x = term();

        while (tkStr.equals("+") || tkStr.equals("-")) {

            int op = Tokenizer.String2Id(tkStr);

            next();

            y = term();
            x = Register.Compute(op, x, y);
            x.setKind(6);
        }
        return x;
    }

    public static void assignment() throws Exception {

        next(); // consume identifier
        String varName = tkStr;
        // retrieve the value of the variable
        next(); // "consume tkstr"
        next(); // consume "<-"
        Result src;
        int nearestWhileHeaderBbp;

        // tkStr could be a function call.
        if (tkStr.equals("call")) {
            next(); //consume function.
            int instructionNo = functionCall();
            src = new Result(0, instructionNo);
            src.setInstructionSp(instructionNo);
            SSA.addAssignmentToSymbolTable(varName, src); // maps a symbol to an instruction in the current bb
            varTable.put(varName, src.getValue());
            if (nestedLoop > 0) {
                nearestWhileHeaderBbp = SSA.skipWhileBody(SSA.getBbp());
                SSA.updatePhiInTargetBlock(varName, src.getInstructionSp(), nearestWhileHeaderBbp);
            }
            next();
        } else {
            // tkStr could be an expression.
            tempTerms = new ArrayList<>();
            src = expression();
            if (tempTerms.size() == 1 && tempTerms.get(0) instanceof Integer) {
                int term = (int) tempTerms.get(0);
                Result x = new Result(0, term);
                x.setInstructionSp(SSA.addConst(term));
                SSA.addAssignmentToSymbolTable(varName, x);
                varTable.put(varName, term);
                if (nestedLoop > 0) {
                    nearestWhileHeaderBbp = SSA.skipWhileBody(SSA.getBbp());
                    SSA.updatePhiInTargetBlock(varName, x.getInstructionSp(), nearestWhileHeaderBbp);
                }
            } else if (tempTerms.size() == 1 && (tempTerms.get(0) instanceof Character || tempTerms.get(0) instanceof String)) {
                String variable = (String) tempTerms.get(0);
                Result x = new Result();
                x.setInstructionSp(SSA.findVariableSkipElseThenBody(variable));
                SSA.addAssignmentToSymbolTable(varName, x);
                if (nestedLoop > 0) {
                    nearestWhileHeaderBbp = SSA.skipWhileBody(SSA.getBbp());
                    SSA.updatePhiInTargetBlock(varName, x.getInstructionSp(), nearestWhileHeaderBbp);
                }
            } else {
                SSA.addAssignmentToSymbolTable(varName, src); // maps a symbol to an instruction in the current bb

                varTable.put(varName, src.getValue());

                if (nestedLoop > 0) {
                    nearestWhileHeaderBbp = SSA.findNextWhileHeader(SSA.getBbp());
                    System.out.println("varName: " + varName + " src: " + src.getValue() + " nearestWhileHeaderBbp: " + nearestWhileHeaderBbp);
                    SSA.updatePhiInTargetBlock(varName, src.getValue(), nearestWhileHeaderBbp);

                }
                System.out.println("reached end");
            }
        }

        if (!varTable.containsKey(varName)) {
            System.out.println("Warning: variable " + varName + " not declared before use.");
        }

    }

    public static void varDecl() throws Exception {
        next(); // consume "var"
        String varName = tkStr;
        next(); // consume identifier
        varTable.put(varName, 0); // declared variables will initialize at -1
        if (tkStr.equals(";")) {
            next();
        } else {
            varDecl();
        }
    }

    public static void statSequence() throws Exception {
        while (!tkStr.equals(".") && !tkStr.equals("else") && !tkStr.equals("fi") && !tkStr.equals("od")) {
            if (tkStr.equals(";")) {
                SSA.printAll();
                next();
            }

            String statement = Tokenizer.Id2String(currTk);
            switch (statement) {

                case "let" -> {
                    assignment();
                    if (tkStr.equals(";")) {
                        next();
                    }
                }

                case "call" -> {
                    // consume call
                    next();
                    // run function call (now at the actual function call)
                    functionCall();
                }
                // function call
                case "if" -> {
                    ifStatement();
                }

                case "while" -> {
                    nestedLoop++;
                    whileLoop();
                    nestedLoop--;
                }

                case "}" -> {
                    next();
                }
                default -> {
                    syntaxError();
                }
            }
        }
    }

    public static void computation() throws Exception {
        // first word was main
        // now at "var"
        next(); // consume "var", now at first variable name
        varDecl(); // now at the next statement
        next();

        statSequence();
        SSA.printAll();
        SSA.generateDotGraph();
    }

    public static void syntaxError() throws Exception {
        throw new Exception("Syntax error at character:" + tkStr);
    }

    public static boolean onlyDigits(String str) {
        for (int i = 0; i < str.length(); i++) {

            if (str.charAt(i) < '0'
                    || str.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }

    public static int functionCall() {
        // returns the instruction number of the function call
        // for ex; let a <- call InputNum will create an instruction 1: read and then
        // add to symbol table the instruction 1 for a

        // inputNum and outputNum.
        if (tkStr.contains("InputNum")) {
            next(); // now at )
            // read instruction at current sp
            // this will return the instruction number of the read instruction to be used by a let var <- inputnum
            return SSA.addRead();
        }

        //outputnum can have an expression in it that will be evaluated and referenced to as the write(instruction #).
        else if (tkStr.contains("OutputNum")) {
            // tkstr right now is OutputNum(.
            next();
            try {
                Result x = expression();
                SSA.addWrite(x);
                next();
            } catch (Exception e) {
                System.out.println("error in outputnum call: " + e);
            }
            // now at ;
        }
        return -1;
    }

    public static boolean isRelOp(String str) {
        return str.equals("<") || str.equals(">") || str.equals("==") || str.equals("!=") || str.equals("<=") || str.equals(">=");
    }

    public static boolean compare(Result x, Result y, String relOp) {
        int xValue = x.getValue();
        int yValue = y.getValue();

        switch (relOp) {
            case "<" -> {
                return xValue < yValue;
            }
            case ">" -> {
                return xValue > yValue;
            }
            case "==" -> {
                return xValue == yValue;
            }
            case "!=" -> {
                return xValue != yValue;
            }
            case "<=" -> {
                return xValue <= yValue;
            }
            case ">=" -> {
                return xValue >= yValue;
            }
            default -> {
                return false;
            }
        }
    }

}