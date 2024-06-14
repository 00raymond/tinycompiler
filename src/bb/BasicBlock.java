package bb;

import instruction.Instruction;

import java.util.ArrayList;
import java.util.HashMap;

public class BasicBlock {
    private ArrayList<Instruction> instructions = new ArrayList<>();
    private HashMap<String, Integer> varTable = new HashMap<>();
    private boolean isJoin = false; // if condition is true, that means the bbp-2 is condition block, bbp-1 is follow thru block
    // this will primarily be used for graphing
    private boolean isThen = false;
    private boolean isElse = false;
    private boolean isWhile = false;
    private boolean isWhileContinue = false;
    private boolean isWhileRel = false;

    public BasicBlock() {

    }

    public void addInstruction(Instruction instruction) {
        instructions.add(instruction);
    }

    public void addSymbol(String symbol, int instructionNo) {
        varTable.put(symbol, instructionNo);
    }

    public HashMap<String, Integer> getVarTable() {
        return varTable;
    }

    public ArrayList<Instruction> getInstructions () {
        return instructions;
    }
    public boolean isJoin() {
        return isJoin;
    }

    public void setJoin(boolean isJoin) {
        this.isJoin = isJoin;
    }

    public boolean isThen() {
        return isThen;
    }

    public void setThen(boolean then) {
        isThen = then;
    }

    public boolean isElse() {
        return isElse;
    }

    public void setElse(boolean anElse) {
        isElse = anElse;
    }

    public boolean isWhile() {
        return isWhile;
    }

    public void setWhile(boolean aWhile) {
        isWhile = aWhile;
    }
    public boolean isWhileRel() {
        return isWhileRel;
    }

    public void setWhileRel(boolean aWhileRel) {
        isWhileRel = aWhileRel;
    }

    public boolean isWhileContinue() {
        return isWhileContinue;
    }

    public void setWhileContinue(boolean aWhileContinue) {
        isWhileContinue = aWhileContinue;
    }

    public void changeInstructionBySymbol(String varName, int newXInstructionNo) {

        if (!varTable.containsKey(varName)) {

            return;
        }

        int instructionNo = varTable.get(varName);

        for (Instruction instruction : instructions) {
            if (instruction.getInstructionNo() == instructionNo) {
                instruction.setX(newXInstructionNo);
                return;
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Basic Block:\n");
        sb.append("Is it a then? ").append(isThen).append("\n");
        sb.append("Is it an else? ").append(isElse).append("\n");
        for (Instruction instruction : instructions) {
            sb.append(instruction.toString()).append("\n");
        }
        return sb.toString();
    }

}
