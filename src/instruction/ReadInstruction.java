package instruction;

public class ReadInstruction extends Instruction {
    private int instructionNo;

    public ReadInstruction(int instructionNo) {
        this.instructionNo = instructionNo;
    }

    @Override
    public String toString() {
        return instructionNo + ": read";
    }
}
