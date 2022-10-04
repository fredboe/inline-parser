package org.parser.alpha;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class AlphaNotationTest {

    private static World resultOfProgram1() throws AlphaError {
        /*
        a_1 := 6
        call fac
        goto end

        a_0 := 1 : fac
        if a_1 = 0 then goto end_fac : loop
        a_0 := a_0 * a_1
        a_1 := a_1 - 1
        goto loop
        return : end_fac
         */
        Map<Register, Value> registers = new HashMap<>();
        Map<Address, Value> memory = new HashMap<>();
        Stack<Value> stack = new Stack<>();

        registers.put(new Register(0), new Value(720));
        registers.put(new Register(1), new Value(0));

        return new World(registers, memory, stack);
    }

    private World resultOfProgram2() throws AlphaError {
        /*
        a_1 := 7
        call fac
        goto end

        // recursive factorial
        a_0 := 1 : fac
        if a_1 = 0 then goto ret : fac_rec
        a_0 := a_0 * a_1
        a_1 := a_1 - 1
        call fac_rec
        return : ret
         */
        Map<Register, Value> registers = new HashMap<>();
        Map<Address, Value> memory = new HashMap<>();
        Stack<Value> stack = new Stack<>();

        registers.put(new Register(0), new Value(5040));
        registers.put(new Register(1), new Value(0));

        return new World(registers, memory, stack);
    }

    private World resultOfProgram3() throws AlphaError {
        /*
        p(1) := 12
        call digit_sum
        goto end

        // p(2) = digit_sum(p(1))
        a_2 := p(1) : digit_sum
        a_0 := 0
        if a_2 = 0 then goto end_digit_sum : loop
        a_1 := a_2 % 10
        a_2 := a_2 / 10
        a_0 := a_0 + a_1
        goto loop
        p(2) := a_0 : end_digit_sum
        return
         */
        Map<Register, Value> registers = new HashMap<>();
        Map<Address, Value> memory = new HashMap<>();
        Stack<Value> stack = new Stack<>();

        registers.put(new Register(0), new Value(3));
        registers.put(new Register(1), new Value(1));
        registers.put(new Register(2), new Value(0));

        memory.put(new Address(1), new Value(12));
        memory.put(new Address(2), new Value(3));

        return new World(registers, memory, stack);
    }

    private World resultOfProgram4() throws AlphaError {
        /*
        a_0 := 1
        a_1 := 4
        push a_0
        push a_1 // hello
        push 6
        stack +
        push 7
        stack *
        pop p(42)
         */
        Map<Register, Value> registers = new HashMap<>();
        Map<Address, Value> memory = new HashMap<>();
        Stack<Value> stack = new Stack<>();

        registers.put(new Register(0), new Value(1));
        registers.put(new Register(1), new Value(4));

        memory.put(new Address(42), new Value(70));

        stack.push(new Value(1));

        return new World(registers, memory, stack);
    }

    private World resultOfProgram5() throws AlphaError {
        /*
        p(1) := 42
        p(p(1)) := 7
        p(1) := 9
        p(p(1)) := p(1)
         */

        Map<Register, Value> registers = new HashMap<>();
        Map<Address, Value> memory = new HashMap<>();
        Stack<Value> stack = new Stack<>();

        memory.put(new Address(1), new Value(9));
        memory.put(new Address(42), new Value(7));
        memory.put(new Address(9), new Value(9));

        return new World(registers, memory, stack);
    }

    private World resultOfProgram6() throws AlphaError {
        /*
        // test
        push 7
        p(12) := 42
        push p(12)
        pop p(11)
        p(11) := 1
        // end
         */

        Map<Register, Value> registers = new HashMap<>();
        Map<Address, Value> memory = new HashMap<>();
        Stack<Value> stack = new Stack<>();

        memory.put(new Address(11), new Value(1));
        memory.put(new Address(12), new Value(42));

        stack.push(new Value(7));

        return new World(registers, memory, stack);
    }

    private World resultOfProgram7() throws AlphaError {
        /*
        p(1) := 42
        call test
        pop p(2)
        p(1) := 2
        goto end

        push p(1) : test
        return
         */

        Map<Register, Value> registers = new HashMap<>();
        Map<Address, Value> memory = new HashMap<>();
        Stack<Value> stack = new Stack<>();

        memory.put(new Address(1), new Value(2));
        memory.put(new Address(2), new Value(42));

        return new World(registers, memory, stack);
    }

    private void testProgram(World worldToTest, World result) throws AlphaError {
        worldToTest.executeProgram();
        assertEquals(worldToTest, result);
    }

    @Test
    public void Test_program_1() throws AlphaError {
        List<String> programLines = List.of(
                "a_1 := 6",
                "call fac",
                "goto end",
                "a_0 := 1 : fac",
                "if a_1 = 0 then goto end_fac : loop",
                "a_0 := a_0 * a_1",
                "a_1 := a_1 - 1",
                "goto loop",
                "return : end_fac"
        );
        testProgram(initiateWorld(programLines), resultOfProgram1());
    }

    @Test
    public void Test_program_2() throws AlphaError {
        List<String> programLines = List.of(
                "a_1 := 7",
                "call fac",
                "goto end",
                "// factorial - recursive",
                "a_0 := 1 : fac",
                "if a_1 = 0 then goto ret : fac_rec",
                "a_0 := a_0 * a_1",
                "a_1 := a_1 - 1",
                "call fac_rec",
                "return : ret"
        );
        testProgram(initiateWorld(programLines), resultOfProgram2());
    }

    @Test
    public void Test_program_3() throws AlphaError {
        List<String> programLines = List.of(
                "p(1) := 12",
                "call digit_sum",
                "goto end",
                "// p(2) = digit_sum(p(1))",
                "a_2 := p(1) : digit_sum",
                "a_0 := 0",
                "if a_2 = 0 then goto end_digit_sum : loop",
                "a_1 := a_2 % 10",
                "a_2 := a_2 / 10",
                "a_0 := a_0 + a_1",
                "goto loop",
                "p(2) := a_0 : end_digit_sum",
                "return"
        );
        testProgram(initiateWorld(programLines), resultOfProgram3());
    }

    @Test
    public void Test_program_4() throws AlphaError {
        List<String> programLines = List.of(
                "a_0 := 1",
                "a_1 := 4",
                "push a_0",
                "push a_1 // hello",
                "push 6",
                "stack +",
                "push 7",
                "stack *",
                "pop p(42)"
        );
        testProgram(initiateWorld(programLines), resultOfProgram4());
    }

    @Test
    public void Test_program_5() throws AlphaError {
        List<String> programLines = List.of(
                "p(1) := 42",
                "p(p(1)) := 7",
                "p(1) := 9",
                "p(p(1)) := p(1)"
        );
        testProgram(initiateWorld(programLines), resultOfProgram5());
    }

    @Test
    public void Test_program_6() throws AlphaError {
        List<String> programLines = List.of(
                "// test",
                "push 7",
                "p(12) := 42",
                "push p(12)",
                "pop p(11)",
                "p(11) := 1",
                "// end"
        );
        testProgram(initiateWorld(programLines), resultOfProgram6());
    }

    @Test
    public void Test_program_7() throws AlphaError {
        List<String> programLines = List.of(
                "p(1) := 42",
                "call test",
                "pop p(2)",
                "p(1) := 2",
                "goto end",
                "",
                "push p(1) : test",
                "return"
        );
        testProgram(initiateWorld(programLines), resultOfProgram7());
    }
    
    private static World initiateWorld(List<String> programLines) throws AlphaError {
        return new World(new Program(programLines));
    }
}
