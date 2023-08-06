import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

//Ryan Havens, Memory Implementation


public class Memory
{
	
	//Create the Memory Stack
	public static int[] memory = new int[2000]; 				//Create memory stack
	public static int memoryIndex = 0; 							// Pointer for memory stack index, start at 0
	public static int temp = 0;									//Temporary variable

	public static void main(String args[]) throws FileNotFoundException {
		String line;
		String sample = args[0];
		Scanner sc = new Scanner(new File(sample));
		String[] lines;
		
		//Load instructions into Memory stack. Get rid of information that is not necessary.
		while(sc.hasNext() && !sc.equals(null)) {			
			
			//Get rid of everything after first space is encountered
			line = sc.nextLine();
			if(line.contains(" ")) {
			lines = line.split(" ");
			line = lines[0];
			}
			line = line.replaceAll(" ", "");
			
			//If the line is blank skip it
			if(line.isBlank()) {							
				continue;
			}			
			
			//If the line contains period. Change the index to store instruction.
			if(line.contains(".")) {						
				line = line.substring(1);
				temp = Integer.parseInt(line);
				memoryIndex = temp;
				continue;
			}
			
			//Store value at said index. Increment memory index pointer.
			memory[memoryIndex] = Integer.parseInt(line); 	
		    memoryIndex++; 									
		   }
		
		
		/*
		//A checker to make sure data is loaded into memory properly
		
		for(int i = 0; i < memory.length; i++) {
			System.out.println(memory[i]);
		}
		*/
		
		
		Scanner s = new Scanner(System.in);
		String cpuInput = null;
		int address;
		int data;

		
		//We will perform this loop of reading and writing instructions received from the CPU until we receive a exit instruction
		while (s.hasNext())
		{
			cpuInput = s.nextLine();
			
			if(cpuInput.charAt(0) == 'r')
			{
				cpuInput = cpuInput.substring(1);
				address = Integer.parseInt(cpuInput);
				read(address);
				continue;
			}

			
			if(cpuInput.charAt(0) == 'w')
			{
				cpuInput = cpuInput.substring(1);
				String[] values = cpuInput.split(" ");
				address = Integer.parseInt(values[0]); 
				data = Integer.parseInt(values[1]);
				write(address,data);
				continue;
			}
			if(cpuInput.charAt(0) == 'e')
			{
				break;
			}
		}
		

	}
	
	//Simple read method to read memory at declared address
	public static void read(int address)
	{
		System.out.println(memory[address]);
		System.out.flush();
	}

	//Write method to write data to said address
	public static void write(int address, int data)
	{
		memory[address] = data;
	}
}
		