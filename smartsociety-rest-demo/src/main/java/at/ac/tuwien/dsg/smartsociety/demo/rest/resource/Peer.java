package at.ac.tuwien.dsg.smartsociety.demo.rest.resource;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Peer", description = "Peer representation")
public class Peer {
	@ApiModelProperty(value = "Peer's name", required = true) private String name;
	@ApiModelProperty(value = "Peer's e-mail address", required = true) private String email;
		
	public Peer() {
	}
	
	public Peer(String email) {
		this.email = email;
	}

    public Peer(String name, String email) {
        super();
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
	
}
