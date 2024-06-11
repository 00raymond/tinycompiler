package instruction;

public class EmptyInstruction extends Instruction {
    private int instructionNo;

    public EmptyInstruction(int instructionNo) {
        this.instructionNo = instructionNo;
        this.op = "empty";
    }
    public int getInstructionNo() {
        return instructionNo;
    }

    public String toString() {
        return instructionNo + ": <empty>";
    }

}
