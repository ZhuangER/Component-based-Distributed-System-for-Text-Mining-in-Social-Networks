package yu.storm.tools;

public class Term {
	private String term;
	private double tfidf;

	public Term(String term, double tfidf) {
		this.term = term;
		this.tfidf = tfidf;
	}

	public String getTerm() {
		return this.term;
	}

	public double getTfidf() {
		return this.tfidf;
	}
}