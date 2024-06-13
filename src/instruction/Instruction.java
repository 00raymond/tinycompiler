package instruction;

import java.util.Objects;

public class Instruction {
    private String instruction;
    private int instructionNo;
    protected String op;
    private int x;
    private int y;

    public Instruction() {}
    public Instruction(int instructionNo, String op, int x, int y) {
        this.instructionNo = instructionNo;
        this.op = op;
        this.x = x;
        this.y = y;
//        instruction = instructionNo + ": " + op + " (" + x + ") (" + y + ")";
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getInstructionNo() {
        return instructionNo;
    }

    public String getOp() {
        return op;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        Instruction instruction = (Instruction) obj;
        return op.equals(instruction.getOp()) && x == instruction.getX() && y == instruction.getY();
//        return false;
    }

    @Override
    public String toString() {
        return instructionNo + ": " + op + " (" + x + ") (" + y + ")";
    }

}
