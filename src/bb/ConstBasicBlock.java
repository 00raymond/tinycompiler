package bb;

import instruction.ConstInstruction;
import instruction.Instruction;

import java.util.ArrayList;

public class ConstBasicBlock extends BasicBlock {
    private ArrayList<Integer> constValues = new ArrayList<>();
    private ArrayList<ConstInstruction> constInstructions = new ArrayList<>();

    public void addInstruction(ConstInstruction instruction) {
        constInstructions.add(instruction);
    }

    public ArrayList<Integer> getConstValues() {
        return constValues;
    }

    public int addConstValue(int value) {
        constValues.add(value);
        return constValues.size() - 1;
    }

    public ArrayList<ConstInstruction> getConstInstructions() {
        return constInstructions;
    }

}
