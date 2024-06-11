import result.Result;

public class Register {

    public static Result Compute(int operator, Result x, Result y) {
        String op = Tokenizer.Id2String(operator);

        String ssaOp = switch (op) {
            case "+" -> "add";
            case "-" -> "sub";
            case "*" -> "mul";
            case "/" -> "div";
            default -> null;
        };

        if (ssaOp == null) {
            throw new IllegalArgumentException("Unsupported operator: " + op);
        }

        Result z = new Result();

        try {
            int instructionSp = SSA.addInstruction(ssaOp, x, y);
            z.setInstructionSp(instructionSp);
            z.setKind(1); // Indicate that this is an instruction result.
            z.setValue(instructionSp); // This value is now the instruction number.
        } catch (Exception e) {
            System.out.println("Error adding instruction in register compute: " + e);
        }

        if (y.getKind() == 0 && x.getKind() == 0) {
            z.setKind(0);
            z.setValue(switch (op) {
                case "+" -> x.getValue() + y.getValue();
                case "-" -> x.getValue() - y.getValue();
                case "*" -> x.getValue() * y.getValue();
                case "/" -> x.getValue() / y.getValue();
                default -> 0;
            });
        }

//        System.out.println("Result: " + z + " (kind: " + z.getKind() + ", value: " + z.getValue() + ", instructionSp: " + z.getInstructionSp() + ")");
        return z;
    }
}
