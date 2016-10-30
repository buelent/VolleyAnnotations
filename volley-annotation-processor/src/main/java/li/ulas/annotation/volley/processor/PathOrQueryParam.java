package li.ulas.annotation.volley.processor;

/**
 * Created by bul on 06.10.16 .
 * ---
 */

public abstract class PathOrQueryParam {
	private String name;
	private String type;
	private String placeholderName;

	public PathOrQueryParam() {
	}

	public PathOrQueryParam(String type, String name) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setPlaceholderName(String placeholderName) {
		this.placeholderName = placeholderName;
	}

	public String getPlaceholderName() {
		return placeholderName;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("PathVariable{");
		sb.append("name='").append(name).append('\'');
		sb.append(", type='").append(type).append('\'');
		sb.append(", placeholderName='").append(placeholderName).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
