package instruction;

public class ConstInstruction extends Instruction {
    private String instruction;
    private int val;
    private int instructionNo;

    public ConstInstruction(int instructionNo, int val) {
        this.instructionNo = instructionNo;
        this.val = val;
        instruction = instructionNo + ": " + "const" + " " + val;
    }

    public int getVal() {
        return val;
    }

    public int getInstructionNo() {
        return instructionNo;
    }

    @Override
    public String toString() {
        return this.instruction;
    }

}
