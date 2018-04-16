import java.io.Serializable;

public class Phrase implements Serializable {
	private String croatianTranslation;
	private String englishTranslation;
	//private image variable;
	Phrase(String croatianTranslation, String englishTranslation) {
		this.croatianTranslation = croatianTranslation;
		this.englishTranslation = englishTranslation;
	}
	
	public String getCroatianTranslation() {
		return croatianTranslation;
	}
	public String getEnglishTranslation() {
		return englishTranslation;
	}
	
	public void setCroatianTranslation(String croatianTranslationInput) {
		croatianTranslation = croatianTranslationInput;
	}
	public void setEnglishTranslation(String englishTranslationInput) {
		englishTranslation = englishTranslationInput;
	}
}
