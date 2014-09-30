package at.ac.tuwien.dsg.smartsociety.demo.rest.resource;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Task", description = "Task representation")
public class Task {
    @ApiModelProperty(value = "Task's id", required = true) private Integer id;
	@ApiModelProperty(value = "Task's title", required = true) private String title;
	@ApiModelProperty(value = "Task's description", required = true) private String description;
		
	public Task() {
	}

    public Task(Integer id, String title, String description) {
        super();
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
	
}
