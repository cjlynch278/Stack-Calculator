//Chris Lynch
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;




public class StackCalculator {
	boolean error = false;
	HashMap<String, Integer> variable = new HashMap<String, Integer>(); 
	//Inititates a Hashmap in order to store variables
	// I got help from the java API http://docs.oracle.com/javase/7/docs/api/java/util/HashMap.html
	
	public StackCalculator(){
	
	}
	public void processInput(String s){
		//This variable is used to flag whenever there is an error
		error =false;
		s = s.replaceAll("\\s+",""); //Removes all spaces, I found the parameters
									// for this function from http://stackoverflow.com/questions/5455794/removing-whitespace-from-strings-in-java
		
		
		ArrayList<String> complete = parse(s);
		complete = clean(complete);
		Queue<String> post = toPostFix(complete);
		//If there's an error, an error message will have already been printed, so just quit out
		// then the result will never be evaluated or printed.
		if(error){
			return;
		}
		 result(post);
	}
	//Parse adds every symbol to the completed List of elements as creating a single element out of a
	//sequence of numbers. If the symbol is not an operator, variable, or number, an error is thrown.
	 public ArrayList<String> parse(String s){
		 
		 ArrayList<String> complete = new ArrayList<String>(); 
		 //An arrayList where the elements of the String will be stored
		 String current=""; 
		 //Current is a string that will be used for holding and building elements that are numbers in string s.
							  
			
			for(int i = 0; i<s.length();i++){
				//I found this Pattern.matches from this thread //http://www.coderanch.com/t/401142/java/java/check-String-numeric
				//If an element of the string is a variable or an operator, add it complete.
				if(Pattern.matches( "([a-zA-Z])",String.valueOf(s.charAt(i)))){
					complete.add(String.valueOf(s.charAt(i)));
					//If the next element is also a letter then an error is thrown
					if(i+1<s.length()&&Pattern.matches( "([a-zA-Z])",String.valueOf(s.charAt(i+1)))){
						System.out.println("Invalid variable name " + s.charAt(i)+s.charAt(i+1));
						error = true;
						return complete;
					} 
					
				}
				else if( s.charAt(i) == '+'|| s.charAt(i)=='-'||s.charAt(i) == '/'|| 
						s.charAt(i) == '*'|| s.charAt(i)== ')'|| s.charAt(i) == '^'||s.charAt(i) == '%' 
						||s.charAt(i) == '('|| s.charAt(i) == '='|| s.charAt(i)=='['|| s.charAt(i) == '{'||
						s.charAt(i)=='['||s.charAt(i)==']'||s.charAt(i)=='}'){
					complete.add(String.valueOf(s.charAt(i)));
									
				}
				//If the element is a number add it to current.
				else if(Pattern.matches( "([0-9]*)",String.valueOf(s.charAt(i)))){
					current += s.charAt(i);
					//If there is another element in s and it's not a number, add current to complete. Then reset current.
					//If there the next element isn't a number then current will keep being built
					if(i+1<s.length()){
						if(!(s.charAt(i+1)== '1'||s.charAt(i+1) =='2'||s.charAt(i+1)== '3'||s.charAt(i+1) =='4'||s.charAt(i+1)== '5'||s.charAt(i+1) =='6'||
							s.charAt(i+1)== '7'||s.charAt(i+1) =='8'||s.charAt(i+1)== '9'||s.charAt(i+1) =='0')){
							complete.add(current);
							current = "";
						
						}
					} 
				}
				//If none of the if statements triggered, it would be because there is an invalid symbol.
				else{
					System.out.println("Error, Invalid Symbol: " + s.charAt(i));
					error = true;
					return complete;
				}
			}
		//If current isn't empty, then we add current to complete.
		if(!current.equals("")){
		complete.add(current);
		}
		return complete;
		
	}
		//This method resolves unary operators, as well as checking for double operator errors such as "4*/3".
		public ArrayList<String> clean(ArrayList<String> complete){
			
			//operatorReoccurence is to check if the last element in complete was an operator
			boolean operatorReoccurence = false;
			//unaryOperator is used to check if the last operator was a + or -
			boolean unaryOperator = false;
			//mod is my new and modified arrayList
			ArrayList<String> mod = new ArrayList<String>();
			
			for(int i = 0; i < complete.size(); i ++){
				
				if(complete.get(i).equals("-")||complete.get(i).equals("+")){
					//If the last element was an operator, change mod accordingly
					if(operatorReoccurence){
						//If the last operator(which would've been the last element) wasn't unary, 
						// it means that the input is nonsensical
						if(!unaryOperator){
							System.out.println("Error, Nonsensical Input "+ complete);
							error = true;
							return complete;
						}
						//Two pluses in a row equates to one plus
						else if(mod.get(mod.size()-1).equals("+")&& complete.get(i).equals("+")){
							mod.remove(mod.size()-1);
							mod.add("+");
						
						}
						//A plus and a minus equate to a minus
						else if(mod.get(mod.size()-1).equals("+") && complete.get(i).equals("-")){
							mod.remove(mod.size()-1);
							mod.add("-");
						}
						else if((mod.get(mod.size()-1).equals("-")&& complete.get(i).equals("+"))){
							mod.remove(mod.size()-1);
							mod.add("-");
						}
						//Two minuses equate to a plus
						else if(mod.get(mod.size()-1).equals("-")&& complete.get(i).equals("-")){
							mod.remove(mod.size()-1);
							mod.add("+");
						}
						
					}
				
					//If the last element was not an operator, add the element to complete
					else{
						mod.add(complete.get(i));
					}
					//This element was an operator
					operatorReoccurence = true;
					unaryOperator = true;
				}
				else if(complete.get(i).equals("(")){
					mod.add("(");
						//if the next operator is a plus or minus, add a zero to calculate unary operators.
						if(i+1<complete.size()&& (complete.get(i+1).equals("-")|| complete.get(i+1).equals("+"))){
							mod.add("0");
						}
						
					operatorReoccurence = false;
					}
				else if(complete.get(i).equals("*")||complete.get(i).equals("/")||
						complete.get(i).equals("%")||complete.get(i).equals("^")){
					//If the last element was an operator, then there will have been two operators in a row.
					if(operatorReoccurence){
						System.out.println("Error, Nonsensical Input" + complete);
						error = true;
						return complete;
					}
					//This element was an operator, but not a unary operator.
					operatorReoccurence =true;
					unaryOperator = false;
					mod.add(complete.get(i));
				}
				
				//If the current element was not an operator, then add it to complete.
				else{
					mod.add(complete.get(i));
					operatorReoccurence = false;
					
				}
					
			}
			
			return mod;
		}
		//This method is to convert an infix string to postfix
		public Queue<String> toPostFix(ArrayList<String> complete){
			//This stack is used to store operations
			Stack<String> operations = new Stack<String>();
			//I used a queue to order correctly. This queue is used to store the result
			Queue<String> postComplete = new LinkedList<String>();
			//I made a constant size
			int size = complete.size();
			
			for(int i = 0; i < size; i++){
				
				//If the current element is a variable, add it to postComplete.
				if(Pattern.matches("[a-zA-Z]+",complete.get(i))){
					postComplete.add(complete.get(i));
				}
				
				//If the current element is a parentheses/bracket modify postComplete accordingly.
				else if(complete.get(i).equals("(")||complete.get(i).equals("[")||complete.get(i).equals("{")
						||complete.get(i).equals(")")||complete.get(i).equals("]")||complete.get(i).equals("}"))
					//If the current element is an open parentheses/bracket, add it to the operations
					if(complete.get(i).equals("(")||complete.get(i).equals("[")||
							complete.get(i).equals("{")){
						
						
						operations.push(complete.get(i));
						
						}
					//If the  current element is a closed bracket/parentheses, pop all operations to postComplete until we hit a similar
					// open bracket, then remove the open bracket from operations
					else if(complete.get(i).equals(")")||complete.get(i).equals("]")||
							complete.get(i).equals("}")){
						
						//This error would be thrown specifically if there were an input such as ")(" 
					  	if(operations.isEmpty()){
							System.out.println("Error, Mismatched Parentheses");
							error = true;
							return postComplete;
						}
					 
						//Make sure the parentheses or brackets match
						while(!((complete.get(i).equals(")") && operations.peek().equals("("))||(operations.peek().equals("[")
								&& complete.get(i).equals("]")) ||(operations.peek().equals("{")&& complete.get(i).equals("}")))){
							
							
							//If there is another parentheses/bracket, then it must have not matched the above loop
							if(operations.peek().equals("(")|| operations.peek().equals("[")|| operations.peek().equals("{")){
								System.out.println("Error, Mismatched Parentheses");
								error = true;
								return postComplete;
							}
							//pop all operations until a matched bracket or parentheses is hit
							postComplete.add(operations.pop());
							//if all operations are popped, then that means this closed bracket should not exist
							if(operations.isEmpty()){
								System.out.println("Error, Too Many Right Parentheses!");
								error = true;
								return postComplete;
							}
							}
						//Remove the open bracket from operations
							operations.pop();	
						} 
					else{
						
					}
				
				//If the current element is an operator, we will add the other operators according to the 
				// precedence of the current operator
				else if(complete.get(i).equals("+")||complete.get(i).equals("-")||complete.get(i).equals("/")
					|| complete.get(i).equals("*")||complete.get(i).equals("^")|| complete.get(i).equals("%")){

					switch(complete.get(i)){
						//I placed each operator in order of descending value. Each case will pop other operators according to precedence
						// then add itself to the stack of operations
						case"^":
							while(!operations.isEmpty()&&(operations.peek().equals("(")|| operations.peek().equals("{")|| operations.peek().equals("["))){
								postComplete.add(operations.pop());
							}
							operations.push(complete.get(i));
							break;
						case "%":
						case "*":
						case "/":
							while(!operations.isEmpty()&& (operations.peek().equals("%") || operations.peek().equals("*")|| 
									operations.peek().equals("/")|| operations.peek().equals("^"))){
									postComplete.add(operations.pop());
								}
							
							operations.push(complete.get(i));
							break;
						case "+":
						case "-":
							while(!(operations.isEmpty())&& !(operations.peek().equals("(")|| operations.peek().equals("{")|| operations.peek().equals("["))){
								postComplete.add(operations.pop());
							}
							operations.push(complete.get(i));
							break;
						
					
						default: 
							System.out.println("Error, Unknown operation");
							System.out.println(complete.get(i));
							error = true;
							return postComplete;
					}
				
				}
				//Since invalid symbols were already checked for in the previous method, any other element
				// must be a number, so we add it to postComplete
				else{
					postComplete.add(complete.get(i));
				}
			

				}
			//Pop all remaining operations. If it's an open parentheses, then there weren't enough closed
			//parentheses for it to have been popped
			while(!operations.isEmpty()){
				if(operations.peek().equals("(")||operations.peek().equals("[")|| operations.peek().equals("{")){
					System.out.println("Error, Too Many Left Parentheses");
					error = true;
					return postComplete;
				}
				postComplete.add(operations.pop());
			}
			return postComplete;
		}
		//This method turns our postfix string into a result as well as replacing
		// any variables with their respective numbers.
		public void result(Queue<String> post){
			//This boolean determines if there has been an equal sign
			boolean equals = false;
			//varName keeps track of the variable name.
			String varName = "";
			
			Stack<Integer> result = new Stack<Integer>();
			//We push a 0 onto the result for the sake of unary operators
			result.push(0);
			
			while(!post.isEmpty()){
				//Push the result depending on the operator, then remove the operator
				if(post.peek().equals("+")){
						result.push(result.pop()+ result.pop());
						post.remove();
				}
				else if(post.peek().equals("%")){
					int right= result.pop();
					int left = result.pop();
					if(right ==0){
						System.out.println("Error mod by zero!");
						return;
					}
					result.push(left%right);
					post.remove();
				}
				else if(post.peek().equals("-")){
					int right= result.pop();
					int left = result.pop();
					result.push(left-right);
					post.remove();
				}
				else if(post.peek().equals("*")){
					result.push( result.pop()* result.pop());
					post.remove();
				}
				else if(post.peek().equals("/")){
					int right= result.pop();
					int left = result.pop();
					if(right == 0){
						System.out.println("Error, Dividing by zero!");
						return;
					}
					result.push(left/right);
					post.remove();
				}
				else if(post.peek().equals("^")){
					int right= result.pop();
					int left = result.pop();
					if(right == 0){
						result.push(1);
					}
					else{
						result.push((int) Math.pow(left,right));
	
					}
					
					post.remove();
					
				}
				//If the current post element is a letter, and the next post element is an equal sign,
				// name the variable accordingly and set equals to true. At the end of the loop we will
				// assign the resulting number to the variable
				else if(Pattern.matches("[a-zA-Z]+",post.peek())){
					
					String left = post.remove();
					if(post.peek().equals("=")){
						post.remove();
						varName = left;
						equals= true;
					}
					//if there is no equal sign and the variable already exists in the hashmap, then
					// push it's value to result, if it doesn't exist in hashmap then an error will be fired.
					else{
						if(variable.containsKey(left)){
							result.push(variable.get(left));
						}
						else{
							System.out.println("Undefined Variable " + left);
							error = true;
							return;
						}
					}
				}
				//If the current element is a number, push it onto result.
				else{
					result.push(Integer.valueOf(post.remove()));
				}
			
			}
			//If result's size is greater than one, then it is because of the zero that 
			// we added at the beginning of this method. Add the two together to get a result.
			if(result.size()>1){
				result.push(result.pop()+result.pop());
			}
			//Assign the final result to the variable or print the final result
			if(equals){
				variable.put(varName, result.peek());
				System.out.println(varName +" is set to " + variable.get(varName) );
				
			}
			//If there was no variable, just print the result
			else{
				System.out.println(result.get(0));
			}
		}
 }
