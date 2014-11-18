package bytecrawl.evtj.server.requests;

public class Request {

    private Client client;
    private String request;

    public Request(Client client, String request) {
        this.client = client;
        this.request = request;
    }

    public String getRequest() {
        return request;
    }

    public Client getClient() {
        return client;
    }
}
