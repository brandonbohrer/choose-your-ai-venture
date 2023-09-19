import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import java.util.Scanner;

import java.util.ArrayList;
import java.util.List;

public class Main {	
    public static void main(String[] args) {
    	Scanner input = new Scanner(System.in);
        String apiKey = System.getenv("API_KEY");
        OpenAiService service = new OpenAiService(apiKey);

        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemMessage = new ChatMessage(
        		ChatMessageRole.SYSTEM.value(),
        		"Your job is to be a narrarator for a cool and fun adventure game for the user. The journey will start"
        		+ " as the user gets off a boat and enters a strange, foreign land. They are searching for hidden treasure. Each response should be no more than 80 words. The game will end"
        		+ " when the user reaches the center of a cave and they find treasure beyond belief. Depending on how efficient the player is, it should"
        		+ " take ROUGHLY 20 user messages to reach the treasure. When the user finds the hidden treasure your next message will say, at the end, 'You have found the hidden treasure!'. "
        		+ "With each response, you will give a message to the user and you will also create a second message that is a description for an AI generated image to go along"
        		+ " with the message that will be given to DALL-E.");
        messages.add(systemMessage);
        
        ChatMessage firstUserMessage = new ChatMessage( //initial message, user does not see
                ChatMessageRole.USER.value(),
                "Where am I?");
        messages.add(firstUserMessage);
        
        while(true) {  	
        	ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model("gpt-3.5-turbo")
                    .temperature(0.6d)
                    .messages(messages)
                    .build();

                ChatCompletionResult chatCompletionResult = service.createChatCompletion(chatCompletionRequest);
                String responseMessage = retrieveResponse(chatCompletionResult);
                
                ChatMessage assistantMessage = new ChatMessage(
                		ChatMessageRole.ASSISTANT.value(),
                		responseMessage);
                messages.add(assistantMessage);
                
        	System.out.println(responseMessage);
        	if (responseMessage.contains("You have found the hidden treasure!")) {
        		System.exit(0);
        		input.close();
        	}       	
        	ChatMessage userMessage = new ChatMessage (
        			ChatMessageRole.USER.value(),
        		input.nextLine());
        	messages.add(userMessage);          
        }      
    }
    
    private static String retrieveResponse(ChatCompletionResult chatCompletionResult) {
    	return chatCompletionResult.getChoices().get(0).getMessage().getContent();	
    }
}
