package instruction;

public class BraInstruction extends Instruction {
    private String instruction;
    private int instructionNo;
    private int branchTo;

    public BraInstruction(int instructionNo, int x) {
        this.instructionNo = instructionNo;
        this.branchTo = x;
        instruction = instructionNo + ": " + "bra" + " (" + x + ")";
    }

    public int getInstructionNo() {
        return instructionNo;
    }

    public int getBranchTo() {
        return branchTo;
    }

    @Override
    public String toString() {
        return this.instruction;
    }
}
