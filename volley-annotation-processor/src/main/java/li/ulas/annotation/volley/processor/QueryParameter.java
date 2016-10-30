package li.ulas.annotation.volley.processor;

/**
 * Created by bul on 06.10.16 .
 * ---
 */

public class QueryParameter extends PathOrQueryParam {
	private boolean isEncoded = false;

	public QueryParameter(String type, String name) {
		super(type, name);
	}

	public boolean isEncoded() {
		return isEncoded;
	}

	public QueryParameter setEncoded(boolean encoded) {
		isEncoded = encoded;
		return this;
	}
}
