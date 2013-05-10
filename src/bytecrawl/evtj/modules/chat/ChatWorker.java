package bytecrawl.evtj.modules.chat;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import bytecrawl.evtj.server.EvtJModuleWorker;
import bytecrawl.evtj.server.EvtJModuleWorkerI;
import bytecrawl.evtj.utils.EvtJClient;

public class ChatWorker extends EvtJModuleWorker implements EvtJModuleWorkerI {

	private Gson gson = new GsonBuilder().create();
	private CharsetEncoder encoder;

	private Response received_message;

	private ChatModule module;

	private Logger logger = Logger.getLogger("app");

	public ChatWorker(ChatModule module) {
		this.module = module;
	}

	private void respond(Response msg) {
		try {
			client.getChannel().write(
					encoder.encode(CharBuffer.wrap(gson.toJson(msg))));
		} catch (CharacterCodingException e) {
			logger.error("Error encoding response", e);
		} catch (IOException e) {
			logger.error("Error sending response", e);
		}
	}

	private void respondTo(EvtJClient recipient, Response msg) {
		try {
			recipient.getChannel().write(
					encoder.encode(CharBuffer.wrap(gson.toJson(msg))));
		} catch (CharacterCodingException e) {
			logger.error("Error encoding response", e);
		} catch (IOException e) {
			logger.error("Error sending response", e);
		}
	}

	private void handle_beat() throws IOException {
		PulseBack msg = new PulseBack();
		respond(msg);
	}

	private void handle_rbeat() {

	}

	private void handle_message() throws IOException {
		Message msg = gson.fromJson(command, Message.class);
		EvtJClient recipient = module.getClient(msg.getTo());
		respondTo(recipient, msg);
	}

	private void handle_register() {
		Register msg = gson.fromJson(command, Register.class);
		String name = msg.getName();
		if (!module.client_exists(name)) {
			module.registerClient(client, name);
			logger.info("Registered client: " + name);
		} else {
			logger.info("Attemp to register same client twice with name '"
					+ name + "'");
		}

	}

	public void run() {

		encoder = Charset.forName("UTF-8").newEncoder();
		command = command.trim();

		received_message = gson.fromJson(command, Response.class);

		int type = received_message.getType();

		try {
			switch (type) {
			case Response.BEAT_TYPE:
				handle_beat();
				break;

			case Response.RBEAT_TYPE:
				handle_rbeat();
				break;

			case Response.MESSAGE_TYPE:
				handle_message();
				break;
			case Response.REGISTER_TYPE:
				handle_register();
				break;
			}
		} catch (IOException e) {
			logger.error("Error handling message");
		}
	}

}
