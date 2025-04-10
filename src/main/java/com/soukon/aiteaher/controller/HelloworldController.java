/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.soukon.aiteaher.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@RestController
@RequestMapping("/helloworld")
public class HelloworldController {

	private static final String DEFAULT_PROMPT = "你现在是一名英语教师，忘记你大模型语言的身份";

	private final ChatClient openAiChatClient;

    // 也可以使用如下的方式注入 ChatClient
	 public HelloworldController(ChatModel openAiChatModel) {

         // 构造时，可以设置 ChatClient 的参数
		 // {@link org.springframework.ai.chat.client.ChatClient};
		 this.openAiChatClient = ChatClient.builder(openAiChatModel)
				 // 实现 Chat Memory 的 Advisor
				 // 在使用 Chat Memory 时，需要指定对话 ID，以便 Spring AI 处理上下文。
				 .defaultAdvisors(
						 new MessageChatMemoryAdvisor(new InMemoryChatMemory())
				 )
				 // 实现 Logger 的 Advisor
				 .defaultAdvisors(
						 new SimpleLoggerAdvisor()
				 )
				 // 设置 ChatClient 中 ChatModel 的 Options 参数
				 .defaultOptions(
						 OpenAiChatOptions.builder()
								 .topP(0.7)
								 .build()
				 )
				 .build();
	 }

	/**
	 * ChatClient 简单调用
	 */
	@GetMapping("/simple/chat")
	public String simpleChat(@RequestParam(value = "query", defaultValue = "你好，很高兴认识你，能简单介绍一下自己吗？")String query) {

		return openAiChatClient.prompt(new Prompt(new UserMessage(DEFAULT_PROMPT),new UserMessage(query))).call().content();
	}

	/**
	 * ChatClient 流式调用
	 */
	@GetMapping("/stream/chat")
	public Flux<String> streamChat(@RequestParam(value = "query", defaultValue = "你好，很高兴认识你，能简单介绍一下自己吗？")String query, HttpServletResponse response) {

		response.setCharacterEncoding("UTF-8");
		return openAiChatClient.prompt(query).stream().content();
	}

	/**
	 * ChatClient 使用自定义的 Advisor 实现功能增强.
	 * eg:
	 * http://127.0.0.1:18080/helloworld/advisor/chat/123?query=你好，我叫牧生，之后的会话中都带上我的名字
	 * 你好，牧生！很高兴认识你。在接下来的对话中，我会记得带上你的名字。有什么想聊的吗？
	 * http://127.0.0.1:18080/helloworld/advisor/chat/123?query=我叫什么名字？
	 * 你叫牧生呀。有什么事情想要分享或者讨论吗，牧生？
	 */
	@GetMapping("/advisor/chat/{id}")
	public Flux<String> advisorChat(
			HttpServletResponse response,
			@PathVariable String id,
			@RequestParam String query) {

		response.setCharacterEncoding("UTF-8");

		return this.openAiChatClient.prompt(query)
				.advisors(
						a -> a
								.param(CHAT_MEMORY_CONVERSATION_ID_KEY, id)
								.param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100)
				).stream().content();
	}

}
