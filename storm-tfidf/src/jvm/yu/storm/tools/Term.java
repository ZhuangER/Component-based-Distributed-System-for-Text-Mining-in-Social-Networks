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
	public void setTerm(String term) {
		this.term = term;
	}

	public void setTfidf(double tfidf) {
		this.tfidf = tfidf;
	}
	public String toString() {
		return this.term + " " + String.valueOf(this.tfidf);
	}
}