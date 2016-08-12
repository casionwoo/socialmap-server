package ssomap.HotKeyWord;

import org.snu.ids.ha.index.Keyword;
import org.snu.ids.ha.index.KeywordExtractor;
import org.snu.ids.ha.index.KeywordList;

public class HotKeyWord {
	
	public static HotKeyWord self = null;
	KeywordExtractor ke = null;
	
	public static HotKeyWord get(){
		if(self == null)
			self = new HotKeyWord();
		return self;
	}
	
	public void setExtractor(){
		ke = new KeywordExtractor();
	}
	
	public KeywordList ExtractKeyword(String strToExtrtKwrd){
		KeywordList kl = ke.extractKeyword(strToExtrtKwrd, true);
		return kl;
	}
	public class Keywordtemp implements Comparable<Keywordtemp>{
		Keyword keyword = new Keyword();

		public Keyword getKeyword() {
			return keyword;
		}

		public void setKeyword(Keyword keyword) {
			this.keyword = keyword;
		}

		@Override
		public int compareTo(Keywordtemp o) {
			// TODO Auto-generated method stub
			return o.keyword.getCnt() - this.keyword.getCnt();
		}
	}
}