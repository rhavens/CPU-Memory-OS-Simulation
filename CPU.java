import java.io.*;
import java.util.Random;
import java.util.Scanner;
import java.lang.Runtime;

//Ryan Havens, CPU Implementation



public class CPU
{
	//Create the registers
	public static int IR;	//Instruction Register
	public static int AC;	//Accumulator Register (Logic/Arithmetic)
	public static int PC;	//Program Counter
	public static int SP;	//Stack Pointer
	public static int X;	//Local Variable
	public static int Y;	//Local Variable
	
	//Create variables for timer and counter for instructions executed
	public static int timer;
	public static int executionCounter;
	
	//Way to declare what type of interrupt (1 for timer, 2 for system call)
	public static int intterupt;
	
	//Helps decide when CPU is fetch/executing and not
	public static boolean state;	
	public static boolean kernel;
	
	//Input/Output Streams
	public static InputStream is;
	public static OutputStream os;
	
	//Scanner and PritWriter
	public static Scanner sc;
	public static PrintWriter pw;
	
	
	public static void main(String args[]) {
      
	PC = 0;				//Start the PC counter at first address in memory
	state = true;		//CPU initially set to run	
	SP = 999;			//Start the stack pointer at the bottom of memory and work up
	timer = Integer.parseInt(args[1]); //Timer is set here. Was unable to implement with arguments
	String textFile = args[0];
	try {            
	
	
	int x;
	Runtime rt = Runtime.getRuntime();
	
	Process proc = rt.exec("java /Users/ryanhavens/eclipse-workspace/Proc/src/Memory.java " + textFile);				//Run the desired program with correct pathing
	
	
	
	is = proc.getInputStream();
	os = proc.getOutputStream();
	
	pw = new PrintWriter(os);
	sc = new Scanner(is);				
	
	
	
	
	//Main fetch and execute block
	
	while(state != false) {
		
		
		IR = fetch();
		execute();
		
		//Interrupt if we time out
		if(executionCounter >= timer && kernel != true) {
			interrupt(1);
		}
		
	}
	
	//We have completed execution, now we will send out exit to Memory.java file to end read and write loop 
	
	pw.println("exit");
	pw.flush();
	//proc.destroy();
	proc.waitFor();
	pw.close();
    os.close();
    
	} 
	catch (Throwable t)
	{
	t.printStackTrace();
 }
}
	
	//Fetch Method
	public static int fetch() {
		int instruction = readMemory(PC);
		PC++;
		return instruction;
		
	}
	
	//Read memory method
	public static int readMemory(int address) {
		if(address >= 1000 && kernel != true) {
			System.out.println("System Memory is not allowed to be accessed");
			System.exit(0);
			return 0;
		}
		pw.println("r" + address);					
		pw.flush();
		String line = sc.nextLine();			
		return Integer.parseInt(line);			
		
	}
	
	//Write Memory Method
	public static void writeMemory(int address, int data) {
		
		pw.println("w" + address + " " + data);
		pw.flush();
		
	}
	
	//Push command for stack
	public static void push(int data) {
		
		writeMemory(SP, data);
		SP--;
		
	}
	
	//Simple Pop method 
	public static int pop() {
		
		SP++;
		int temp = readMemory(SP);
		return temp;
		
	}
	
	//Interrupt method for timer and instruction interrupt
	public static void interrupt(int value) {
		
		kernel = true;
		int userSP = SP;
		
		SP = 1999;
		
		push(userSP);
		push(PC);
		
		switch(value) {
		case 1:					//Timer interrupt
			PC = 1000;
			break;
		case 2:					//System Call
			PC = 1500;
			break;
		}
	}
	
	//Instruction Set
	public static void execute() {	
		
		int z;
		int y;
		
		switch (IR){
		
		case 1:
			//Load value into AC 
			AC = fetch();
			break;
		case 2:
			//Load value at address address into AC;
			z = fetch();
			y = readMemory(z);
			AC = y;
			break;
		case 3:
			//Load index into value
			z = fetch();
			y = readMemory(z);
			y = readMemory(y);
			AC = y;
			break;
		case 4:
			//Load value at address into AC
			z = fetch();
			z = z + X;
			z = readMemory(z);
			AC = z;
			break;
		case 5:	
			;//Load index into value
			z = fetch();
			z = readMemory(z + CPU.Y);
			AC = z;
			break;
		case 6:
			//Load address from stack pointer + x into AC
			z = SP + X;
			AC = readMemory(SP + X + 1);
			break;
		case 7:
			//Store value in AC into address
			z = fetch();
			writeMemory(z, AC);
			break;
		case 8:								
			//Get random value from 1-100. Put in AC.
			Random randI = new Random();
	        int myRandInt = randI.nextInt(100);
	        myRandInt = myRandInt+1;
			AC = myRandInt;
			break;
		case 9:								
			//If 1 put into integer value, if 2 put into character value
			z = fetch();
			if (z == 1)
				System.out.print(AC);
			else if (z == 2)
				System.out.print((char) AC);
			break;
		case 10:
			//Add value in X to AC
			AC += X;
			break;
		
		case 11:							
			//Add value of Y to AC
			AC += Y;
			break;
		case 12:							
			//Subtract value of X from AC
			AC -= X;
			break;
		case 13:							
			//Subtract value of Y from AC
			AC -= Y;
			break;
		case 14:							
			//Copy to X from AC
			X = AC;
			break;
		case 15:							
			//Copy from X to AC
			AC = X;
			break;
		case 16:							
			//Copy to Y from AC
			Y = AC;
			break;
		case 17:							
			//Copy from Y to AC
			AC = Y;
			break;
		case 18:							
			//Copy AC to SP
			SP = AC;
			break;
		case 19:
			AC = SP;						
			//Copy SP to AC
			break;
		case 20:
			//Jump to address
			z = fetch();
			PC = z;
			break;
		case 21:
			//Jump to address if AC = 0
			z = fetch();
			if(AC == 0){
				PC = z;
			}
			break;
		case 22:
			//Jump to address if AC does not equal 0
			z = fetch();
			if(AC != 0 )
			{
				PC = z;
			}	
			break;
		case 23:
			//Push return address onto stack, jump to address
			z = fetch();
			push(PC);
			PC = z;
			break;
		case 24:
			//Pop return address from stack, jump to address
			z = pop();
			PC = z;
			break;
		case 25:
			//Increment value of X
			 X++;
			break;
		case 26:
			//Decrement X
			X--;
			break;
		case 27:
			//Push AC onto stack
			push(AC); 
			break;
		case 28:
			//Pop from stack into AC
			z = pop();
			AC = z;
			break;
		case 29:
			//System call interrupt
			interrupt(2);
			break;
		case 30:
			//Return from system call							//Return from System call
			PC = pop();
			SP = pop();
			kernel = false;
			executionCounter = 0;
			break;
		case 50:
			//End
			state = false;
			break;
		}
		
		executionCounter++;
	}

}

