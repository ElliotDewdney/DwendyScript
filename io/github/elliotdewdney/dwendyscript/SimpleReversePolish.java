package io.github.elliotdewdney.dwendyscript;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class SimpleReversePolish {
    private HashMap<String, Routine> Operation = new HashMap<>();
    private Stack<Double> doubleStack = new Stack<>();
    interface Calculate{double[] Op(Pipe pipe, double[] x);}
    private Pipe pipe;

    public SimpleReversePolish(){
        Operations.values();
    }

    public static void RunTerminalCalculator(){
        new SimpleReversePolish().TerminalCalculator();
    }

    public final String DEFAULT_ERROR = "Function Evaluation";
    private String ERROR = DEFAULT_ERROR;

    public static class Pipe {
        private Queue<Double> tempStorage = new ArrayDeque<>();

        public Pipe(DoubleInput DIn, DoubleOutput DOut){
            DoubleOut = DOut;
            DoubleIn = DIn;
        }
        public Pipe(){}

        public GetSocket SocketGetter;

        public IntOutput IntOut = ((int i) -> System.out.println("Integer Output from program : " + i));
        public DoubleOutput DoubleOut = ((Double i) -> System.out.println("Numerical Output form program : " + i));
        public StringOutput StringOut = ((String i) -> System.out.println("String Output from program : " + i));
        public StringOutput Message = ((String i) -> System.out.println("Debug - " + i));

        public Waiter Wait  = (() -> {System.out.print("Program Waiting (Enter to proceed)");new Scanner(System.in).nextLine();});
        public StringInput StringIn = (() -> {System.out.print("Program String input :");return new Scanner(System.in).nextLine();});
        public DoubleInput DoubleIn = (() -> {System.out.print("Program Numerical input :");return new Scanner(System.in).nextDouble();});
    }

    interface GetSocket{
        Socket getClientSocket() throws IOException;
        ServerSocket getSeverSocket() throws IOException;
    }

    interface IntOutput{void Push(int out);}
    interface DoubleOutput{void Push(Double out);}
    interface StringOutput{void Push(String out);}

    interface Waiter{void waiter();}
    interface StringInput{String Poll();}
    interface DoubleInput{Double Poll();}

    interface action{
        String getDefaultSymbol();
    }

    interface Routine{
        double[] Evaluate(Pipe pipe, double[] arr);
        int getInputs();
    }

    private void initializeOperations(Queue<String> inputs, boolean Debug, double answer){
        for(Operations Op : Operations.values())for(String Sy : Op.Symbol)Operation.put(Sy,Op);
        
        Operation.put("ANS", new Routine() {
            @Override
            public double[] Evaluate(Pipe pipe, double[] arr) {
                return new double[]{answer};
            }
            @Override
            public String toString(){
                return "getAnswer";
            }
            @Override
            public int getInputs() {
                return 0;
            }
        });
        Operation.put("(", new Routine() {
            @Override
            public double[] Evaluate(Pipe pipe, double[] arr) {
                if(Debug) pipe.Message.Push("Starting IF Statement with value : " + doubleStack.pop());
                if(arr[0] != 0 ){
                    int brackets = 1;
                    while(brackets > 0){
                        String data = inputs.poll();
                        brackets += data.equals("(")|| data.equals("!(")? 1 : data.equals(")")? -1 : 0;
                    }
                }
                return new double[0];
            }
            @Override
            public String toString(){
                return "IfStatement";
            }
            @Override
            public int getInputs() { return 1; }
        });
        Operation.put("!(", new Routine() {
            @Override
            public double[] Evaluate(Pipe pipe, double[] arr) {
                if(Debug) pipe.Message.Push("Starting IF Statement");
                if(arr[0] == 0 ){
                    int brackets = 1;
                    while(brackets > 0){
                        String data = inputs.poll();
                        brackets += data.equals("(")|| data.equals("!(")? 1 : data.equals(")")? -1 : 0;
                    }
                }
                return new double[0];
            }
            @Override
            public String toString(){ return "NotIfStatement"; }
            @Override
            public int getInputs() { return 1; }
        });
        Operation.put(")", new Routine() {
            @Override
            public double[] Evaluate(Pipe pipe, double[] arr) {
                return new double[0];
            }
            @Override
            public String toString(){ return "Ending IfStatement"; }
            @Override
            public int getInputs() { return 0; }
        });
        Operation.put("{", new Routine() {
            @Override
            public double[] Evaluate(Pipe pipe, double[] arr) {
                int curlyBraces = 1;
                Queue<String> newInputs = new ArrayDeque<>();
                boolean Not = false;
                do{
                    String next = inputs.peek();
                    curlyBraces += next.equals("{")? 1 : next.equals("}")||next.equals("!}")? -1 : 0;
                    if(curlyBraces != 0)newInputs.add(inputs.poll());
                    else Not =inputs.poll().equals("}");
                }while(curlyBraces > 0);
                if(Debug)pipe.Message.Push("Loop Started");
                double value;
                do{
                    value = Calculator(answer, Debug, new ArrayDeque<>(newInputs));
                    if(Debug)pipe.Message.Push("Loop Run");
                }while((value != 0) == Not);
                return new double[0];
            }
            @Override
            public String toString(){ return "LoopStart"; }
            @Override
            public int getInputs() { return 0; }
        });
        Operation.put("$", new Routine() {
            @Override
            public double[] Evaluate(Pipe pipe, double[] arr) {
                String i = inputs.poll();
                double[] k = new double[i.length()];
                if(Debug) pipe.Message.Push("String " + i + "Pushed to stack");
                for(int j = 1;j<=i.length();j++)k[j-1] = i.charAt(i.length()-j);
                return k;
            }
            @Override
            public String toString(){ return "StringIndicator"; }
            @Override
            public int getInputs() { return 0; }
        });
        Operation.put("SUB", new Routine() {
            @Override
            public double[] Evaluate(Pipe pipe, double[] arr) {
                String name = inputs.poll();
                if(Debug) pipe.Message.Push("Creating New SubRoutine " + name);
                final Queue<String> SubRoutine = new ArrayDeque<>();
                String Routine ="";
                if(inputs.poll().equals("\""))
                    while(!inputs.peek().equals("\"")){
                        Routine = Routine.concat(" " + inputs.peek());
                        SubRoutine.add(inputs.poll());
                    }
                if(inputs.poll().equals("\"") && Debug) pipe.Message.Push("Subroutine \" " + Routine + " \" Created Successfully");
                Operation.put(name, new Routine() {
                    @Override
                    public double[] Evaluate(Pipe pipe, double[] arr) {
                        if(Debug)pipe.Message.Push("Running Subroutine " + name);
                        return new double[]{Calculator(0,Debug,SubRoutine)};
                    }
                    @Override
                    public String toString(){ return name; }
                    @Override
                    public int getInputs() { return 0; }
                });
                return new double[0];
            }
            @Override
            public String toString(){ return "CreateSubroutine"; }

            @Override
            public int getInputs() { return 0; }
        });

    }

    public enum Functions implements action{
        Loop("{  }"),
        NotLoop("{  !}"),
        IfStatment("(  )"),
        NotIfStatment("(  !)"),
        Answer("ANS->"),
        String(" $ ExampleString"),
        SubRoutine("SUB name \" code \""),
        Comment("# text #");
        ;

        Functions(String symbol){
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return name() + " => [ " + symbol + " ]";
        }

        String symbol;

        @Override
        public String getDefaultSymbol() {
            return symbol;
        }
    }

    public enum Operations implements action, Routine{

        add( 2, (Pipe pipe, double[] x) -> new double[]{x[0] + x[1]}, "+"),
        subtract( 2, (Pipe pipe, double[] x) -> new double[]{x[1] - x[0]}, "-"),
        multiply( 2, (Pipe pipe, double[] x) -> new double[]{x[0] * x[1]}, "*"),
        divide( 2, (Pipe pipe, double[] x) -> new double[]{x[1] / x[0]}, "/"),
        power( 2, (Pipe pipe, double[] x) -> new double[]{Math.pow(x[1] , x[0])}, "^"),
        mod( 2, (Pipe pipe, double[] x) -> new double[]{x[1] % x[0]}, "%"),
        duplicate( 1, (Pipe pipe, double[] x) -> {double[] i = {x[0] , x[0]};return i;}, "DUPE", "&&"),
        swap( 2, (Pipe pipe, double[] x) -> {double[] i = {x[0], x[1]};return i;}, "SWAP", "<->"),
        minus( 1, (Pipe pipe, double[] x) -> {double[] i = {-x[0]};return i;}, "~"),
        log( 2, (Pipe pipe, double[] x) -> {double[] i = {Math.log10(x[0])/Math.log10(x[1])};return i;}, "LOG"),
        EngineersNotation( 2, (Pipe pipe, double[] x) -> {double[] i = {x[1]*Math.pow(10, x[1])};return i;}, "x10^" , "E"),
        Rounding( 2, (Pipe pipe, double[] x) -> {double[] i = {((double)Math.round(x[1] * Math.pow(10,x[0])))/Math.pow(10,x[0])};return i;}, "ROUND" , ".?"),
        toInteger( 1, (Pipe pipe, double[] x) -> {double[] i = {((int)x[0])};return i;}, "<I>"),
        sin( 1, (Pipe pipe, double[] x) -> {double[] i = {Math.sin(x[0])};return i;}, "sin"),
        arcsin( 1, (Pipe pipe, double[] x) -> {double[] i = {Math.asin(x[0])};return i;}, "arcsin"),
        cos( 1, (Pipe pipe, double[] x) -> {double[] i = {Math.cos(x[0])};return i;}, "cos"),
        arccos( 1, (Pipe pipe, double[] x) -> {double[] i = {Math.acos(x[0])};return i;}, "arccos"),
        tan( 1, (Pipe pipe, double[] x) -> {double[] i = {Math.tan(x[0])};return i;}, "tan"),
        arctan( 1, (Pipe pipe, double[] x) -> {double[] i = {Math.atan(x[0])};return i;}, "arctan"),
        squareRoot( 1, (Pipe pipe, double[] x) -> {double[] i = {Math.sqrt(x[0])};return i;}, "sqr", "sqrt"),
        pi( 0, (Pipe pipe, double[] x) -> {double[] i = {Math.PI};return i;}, "pi"),
        e( 0, (Pipe pipe, double[] x) -> {double[] i = {Math.E};return i;}, "e"),
        randomNum( 2, (Pipe pipe, double[] x) -> {double[] i = {Math.min(x[0],x[1])+(Math.random()*Math.abs(x[0]-x[1]))};return i;}, "RAND", "??"),
        min( 2, (Pipe pipe, double[] x) -> {double[] i = {Math.min(x[0], x[1])};return i;}, "min"),
        max( 2, (Pipe pipe, double[] x) -> {double[] i = {Math.max(x[0], x[1])};return i;}, "max"),
        compare( 2, (Pipe pipe, double[] x) -> { if(x[1]>x[0]){double[] c = {x[1]}; return c;} else return new double[]{}; }, "cmp"),
        binaryCompare( 2, (Pipe pipe, double[] x) -> {double[] i = {(x[1]>x[0])? 1 : 0};return i;}, "bin>", "bincmp"),
        binaryEquals( 2, (Pipe pipe, double[] x) -> {double[] i = {(x[1]==x[0])? 1 : 0};return i;}, "bin=="),
        input( 0, (Pipe pipe, double[] x) -> {double[] i = {pipe.DoubleIn.Poll()};return i;}, "U->", "->", "in"),
        wait( 0, (Pipe pipe, double[] x) -> {pipe.Wait.waiter();return new double[]{};}, "W->", "W", "W"),
        StringInput( 0, (Pipe pipe, double[] x) -> {String i = pipe.StringIn.Poll();double[] y = new double[i.length()];for(int j = 1;j<=i.length();j++)y[j-1] = i.charAt(i.length()-j);return y;}, "S->", "SIn"),
        output( 1, (Pipe pipe, double[] x) -> {pipe.DoubleOut.Push(x[0]);return new double[]{};}, "U<-", "<-", "out"),
        removeValue( 1, (Pipe pipe, double[] x) -> {return new double[]{};}, "X<-"),
        outputInt( 1, (Pipe pipe, double[] x) -> {pipe.IntOut.Push((int)x[0]);return new double[]{};}, "I<-", "IOut"),
        outputChar( 1, (Pipe pipe, double[] x) -> {pipe.StringOut.Push(String.valueOf((char)x[0]));return new double[]{};}, "C<-", "COut"),
        printT( 0, (Pipe pipe, double[] x) -> {String i = ""; while(!pipe.tempStorage.isEmpty())i = i.concat(String.valueOf((char)(double)pipe.tempStorage.poll()));pipe.StringOut.Push(i);return new double[]{};}, "S<-T", "SOut"),
        pushToTemp( 1, (Pipe pipe, double[] x) -> {pipe.tempStorage.add(x[0]);return new double[]{};}, "T<-", "PUSH"),
        pullFromTemp( 0, (Pipe pipe, double[] x) -> new double[]{pipe.tempStorage.poll()}, "T->", "PULL");

        private Calculate Calc;
        public int inputs;
        public String[] Symbol;
        Operations(int inputs, Calculate Calc, String... symbol){
            this.Calc = Calc;
            this.inputs = inputs;
            this.Symbol = symbol;
        }

        public String toString(){
            return name() + " => [ " + Symbol[0] + " ] ( inputs = " + inputs + " )";
        }

        @Override
        public String getDefaultSymbol() {
            return Symbol[0];
        }

        @Override
        public double[] Evaluate(Pipe pipe, double[] arr) {
            return Calc.Op(pipe, arr);
        }

        @Override
        public int getInputs() {
            return inputs;
        }
    }

    public enum programs{
        pythagoras("-> 2 ^ -> 2 ^ + sqr U<-"),
        largestOfThree("-> -> -> max max"),
        countDownFrom("-> && T<- -> T<- I<- { 1 ~ T-> + && && && T<- I<- T-> && T<- - }"),
        helloWorld("104 T<- 101 T<- 108 T<- 108 T<- 111 T<- 32 T<- 119 T<- 111 T<- 114 T<- 108 T<- 100 T<- S<-T"),
        helloWorldSimple("0 $ World 32 $ Hello { T<- && } S<-T"),
        helloName("0 S-> 32 $ Hello { T<- && } S<-T"),
        primeNumberSearch("2 T<- 0 T<- 3 && && I<- { T-> { && T<- % 0 bin== && && ( X<- X<- && T-> && 0 ) !( 0 ) } && ( X<- 80 C<- && T<- 0 ) !( { T-> && !( T<- 1 ) } ) 0 T<- 1 + && && W I<- 1 }"),
        passwordHasher("{ 0 S-> { + <-> && } X<- 2 ^ 829 % I<- 1 }"),
        LoginSystem("-3 300 0 $ USER -2 183 0 $ Elliot -2 297 0 $ ADMIN 0 $ INPUT_USERNAME { T<- && } S<-T S-> { T<- && } T<- { { T-> && T<- bin== ( -1 0 ) && } X<- && -1 bin== && ( X<- 0 S-> { + <-> && } X<- 2 ^ 829 % bin== !( 80 C<- ) 0 0 ) !( { T-> && !( T<- 1 ) } { -2 bin== !} 1 ) }"),
        
        //quadraticFormula_NOTWORKING("<- && T<- <- <-> <- 4 * * ~ T<- && T<- 2 ^ T-> T<- T-> + sqr && T-> ~ && T<- + T-> 2 * && T<- SWAP / -> T-> + T-> SWAP / -> 0"),
        ;
        public String program;
        programs(String program){
            this.program = program;
        }
    }

    public Pipe getDataPipe(){
        return pipe;
    }

    public Double DoubleInputCalculator(String calculation, DoubleInput DIn){
        Pipe pipe = new Pipe();
        pipe.DoubleIn = DIn;
        return AdvancedCalculation(new ArrayDeque<>(Arrays.asList(calculation.split(" "))), pipe);
    }

    public Double SimpleCalculation(String calculation){
        return AdvancedCalculation(new ArrayDeque<>(Arrays.asList(calculation.split(" "))), new Pipe());
    }

    public Double AdvancedCalculation(Queue<String> Instructions, Pipe pipe){
        return AdvancedCalculation(Instructions, 0 , false, pipe);
    }

    public Double AdvancedCalculation(Queue<String> Instructions, double answer, boolean Debug, Pipe pipe){
        this.pipe = pipe;
        return Calculator(answer, Debug, Instructions);
    }

    private Double Calculator(double answer, boolean Debug, Queue<String> inputs){
        initializeOperations(inputs, Debug, answer);
        try {
            while (!inputs.isEmpty()) try {
                String temp = inputs.poll();
                System.out.println("Current Output : " + temp);
                if (Operation.containsKey(temp)) {
                    Routine Op = Operation.get(temp);
                    if(Debug)pipe.Message.Push("Performing Operation \"" + Op + "\"");
                    double[] items = new double[Op.getInputs()];
                    try {
                        for (int i = 0; i < Op.getInputs(); i++) items[i] = doubleStack.pop();
                    }catch (Exception e){
                        ERROR = "Empty Stack";
                        throw e;
                    }
                    double[] newItems = {};
                    try {
                        newItems = Op.Evaluate(pipe, items);
                    }catch (Exception e){
                        ERROR = "Instruction Evaluation";
                        throw e;
                    }
                    String out = "@";
                    for (double i : newItems) {
                        doubleStack.push(i);
                        out = out.concat(", " + i);
                    }
                    if(Debug)pipe.Message.Push("Pushing -> "+ (out.equals("@")? "Nothing" : out.replaceAll("@, ", "")) + " <- to the Stack");
                } else {
                    try {
                        doubleStack.push(Double.parseDouble(temp));
                        if (Debug) pipe.Message.Push("Debug - Pushing " + temp + " to the stack");
                    } catch (Exception e) {
                        if (Debug) pipe.Message.Push("Debug - Symbol " + temp + " is not recognised");
                        else pipe.Message.Push("Found unrecognised symbol");
                        pipe.Message.Push("The Program has encountered a ERROR during RUNTIME");
                        return null;
                    }
                }
            }catch (Exception e){
                if(Debug){
                    pipe.Message.Push("A Error Occurred due to");
                    pipe.Message.Push("  -> " + ERROR);
                    ERROR = DEFAULT_ERROR;
                }
                throw e;
            }
        }catch (Exception e){
            pipe.Message.Push("The Program has encountered a ERROR during RUNTIME");
            if(Debug) e.printStackTrace();
            return null;
        }
        return doubleStack.empty()? (double)0 : doubleStack.pop();
    }

    public static String GetInfoText(){
        StringBuilder info = new StringBuilder("Welcome to Dwendy's Reverse Polish Scripting language ( DwendyScript )\n\r");
        info.append("This language is based on the reverse polish notation, \n\r" +
                "instruction are executed in order\n\r" +
                "any number(Using Doubles) is pushed operationalStack\n\r " +
                "and any operation takes acts on the Stack\n\r" +
                "T is a temporary Queue data structure Can pe pushed/pulled or outputted as String\n\r" +
                "\n\r------------------------\n\r" +
                "Current instructions include : \n\r" +
                "\n\r");
        for(Operations operation : Operations.values()) info.append(operation.toString()).append("\n\r");
        info.append("\n\r------------------------\n\rAnd Functions include : \n\r");
        for(Functions Func : Functions.values()) info.append(Func.toString()).append("\n\r");
        return info.toString();
    }

    public void TerminalCalculator(){
        pipe = new Pipe();
        System.out.println(GetInfoText());
        double answer = 0;
        boolean DEBUGMODE = false;
        boolean RUNNING = true;
        while (RUNNING){
            System.out.println("\n\r" +
                    "===============================================================================\n\r"+
                    "Input your calculation, L to load program, X to escape or D to toggle DEBUGMODE");
            String input = new Scanner(System.in).nextLine();
            Double temp = null;
            switch (input){
                case "X": case "x":
                    System.out.println("Leaving programing environment ...");
                    RUNNING = false;
                    break;
                case "L": case "l":
                    for(programs program : programs.values()) System.out.println(program.ordinal()+1 + ". "+ program + " [ " + program.program + " ] ");
                    System.out.println("\n\r -- Enter the program name/number to be run -- ");
                    String prog = new Scanner(System.in).nextLine();
                    programs program;
                    try{ program = SimpleReversePolish.programs.values()[Integer.parseInt(prog)-1]; }
                    catch (Exception e){ try{ program = programs.valueOf(prog); }
                    catch (Exception x){
                        System.out.println("No such program");
                        continue;
                    }}
                {Queue<String> stringStack = new ArrayDeque<>(Arrays.asList(program.program.split(" ")));
                    temp = Calculator(answer, DEBUGMODE, stringStack);}
                break;
                case "D": case "d":
                    DEBUGMODE = !DEBUGMODE;
                    System.out.println("Debug mode is " + (DEBUGMODE? "Active" : "Disabled"));
                    break;
                default:
                {Queue<String> stringStack = new ArrayDeque<>(Arrays.asList(input.split(" ")));
                    temp = Calculator(answer, DEBUGMODE, stringStack);}
                break;
            }
            doubleStack = new Stack<>();
            pipe.tempStorage = new ArrayDeque<>();
            if(temp != null){
                System.out.println("\n\r================\n\r{Program ended with ExitCode: " + Math.round(temp.doubleValue()) +"}");
                answer = temp;
            }
        }
    }
}