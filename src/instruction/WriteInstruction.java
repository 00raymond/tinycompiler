package instruction;

public class WriteInstruction extends Instruction {
    private int targetInstructionNo;
    private int instructionNo;
    public WriteInstruction(int instructionNo, int targetInstructionNo) {
        this.instructionNo = instructionNo;
        this.targetInstructionNo = targetInstructionNo;
        super.op = "write";
    }

    public int getTargetInstructionNo() {
        return targetInstructionNo;
    }

    @Override
    public String toString() {
        return instructionNo + ": write(" + targetInstructionNo + ")";
    }

}
