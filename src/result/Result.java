package result;

public class Result {
    int kind; // 0 const, 1 var, 2 reg, 3 condition, 4 error, 5 boolean, 6 INSTRUCTION.
    int value; // value if its a constant, or var idx if variable
    int instructionSp = -1;
    int address; // address if its a variable
    int regno; // register number if its a register

    public Result() { }

    public Result(int kind) {
        this.kind = kind;
    }

    public Result(int kind, int value) {
        this.kind = kind;
        this.value = value;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public void setRegno(int regno) {
        this.regno = regno;
    }

    public int getKind() {
        return kind;
    }

    public int getValue() {
        return value;
    }

    public int getAddress() {
        return address;
    }

    public int getRegno() {
        return regno;
    }

    public void setInstructionSp(int instructionSp) {
        this.instructionSp = instructionSp;
    }

    public int getInstructionSp() {
        return instructionSp;
    }
}
