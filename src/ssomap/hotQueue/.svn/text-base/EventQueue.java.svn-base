package ssomap.hotQueue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;

import org.snu.ids.ha.index.KeywordList;

import ssomap.Photos.Photos;

public class EventQueue {
	public class Counter implements Comparable<Counter> {
		private String geocell;
		private int count;

		public Counter(Item item) {
			geocell = item.geocell;
			count = 1;
		}

		public Counter( Counter temp) {
			this.count = 1;
			this.geocell = temp.getGeocell();
		}

		public String getGeocell() {
			return geocell;
		}

		public void setGeocell(String geocell) {
			this.geocell = geocell;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public int increase() {
			this.count++;
			return this.count;
		}

		public int decrease() {
			this.count--;
			return this.count;
		}

		@Override
		public int compareTo(Counter o) {
			return o.count - this.count;
		}
	}

	public class Item {
		private String id;
		private String geocell;
		private String owner;
		private Date dateTime;
		private KeywordList keywordList;

		public KeywordList getKeywordList() {
			return keywordList;
		}

		public void setKeywordList(KeywordList keywordList) {
			this.keywordList = keywordList;
		}

		public Item(String id, String geocell, String owner, Date dateTime
				) {
			this.id = id;
			this.geocell = geocell;
			this.dateTime = dateTime;
			//this.keywordList = keywordList;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getGeocell() {
			return geocell;
		}

		public void setGeocell(String geocell) {
			this.geocell = geocell;
		}

		public String getOwner() {
			return owner;
		}

		public void setOwner(String owner) {
			this.owner = owner;
		}

		public Date getDateTime() {
			return dateTime;
		}

		public void setDateTime(Date dateTime) {
			this.dateTime = dateTime;
		}
		
	}

	private static EventQueue self = null;

	public static EventQueue get() {
		if (self == null) {
			self = new EventQueue();
		}
		return self;
	}

	public int LIMIT_SIZE = 1024;
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssZ");
	public static final int GEO_CELL_IDX = 9;

	private List<Item> que = null;// new HashMap<String, Integer>();
	private Map<String, Counter> counterMap = new HashMap<String, EventQueue.Counter>();
	//private Map<String, HotKeyWord.Keywordtemp> wordConterMap = new HashMap<String, HotKeyWord.Keywordtemp>();
	private Map<String, Counter> coolCounter = new HashMap<String, EventQueue.Counter>();


	public List<Counter> getCoolCounter(){
		if(coolCounter.values().isEmpty())
			return null;
		List<Counter> list = new ArrayList<Counter>(coolCounter.values());
		return list;
	}
	public void CoolCounterClear(){
		coolCounter.clear();
	}
	private EventQueue() {
		que = new ArrayList<EventQueue.Item>();
	}

	public void push(Photos photo) {
		System.out.println("Push()");
		if (que.size() == LIMIT_SIZE) {
			que.remove(0);
		}

		Item item = convert(photo);
		Counter counter = null;

		Calendar now = Calendar.getInstance();
		now.roll(Calendar.HOUR, -1);
		Date baseTime = now.getTime();

		if( item.getDateTime().after(baseTime)){
			System.out.println("hotCounter Photo");
			if (counterMap.containsKey(item.getGeocell())) {
				counter = counterMap.get(item.getGeocell());
				counter.increase();
				counterMap.remove(item.getGeocell());
				counterMap.put(item.getGeocell(), counter);
			} else {
				counter = new Counter(item);
				counterMap.put(item.getGeocell(), counter);
			}
			que.add(item);
		}

		
		if (coolCounter.containsKey(item.getGeocell())) {
			System.out.println("coolCounter photo");
			counter = coolCounter.get(item.getGeocell());
			counter.increase();
			coolCounter.remove(item.getGeocell());
			coolCounter.put(item.getGeocell(), counter);
		} else {
			counter = new Counter(item);
			coolCounter.put(item.getGeocell(), counter);
		}

		/*		for (Keyword word : item.getKeywordList()) {
			if (wordCounterMap.containsKey(word.getString())) {
				Keywordtemp temp = wordCounterMap.get(word.getString());
				temp.getKeyword().setCnt(
						temp.getKeyword().getCnt() + word.getCnt());
				wordCounterMap.remove(word.getString());
				wordCounterMap.put(word.getString(), temp);
			} else {
				HotKeyWord a = new HotKeyWord();
				HotKeyWord.Keywordtemp temp = a.new Keywordtemp();
				temp.setKeyword(word);
				wordCounterMap.put(word.getString(), temp);
			}
		}*/
		rotate();
	}



	public void doRotate() {
		System.out.println("doRotate");
		rotate();
	}

	public String getTopCell() {
		if (self.isEmpty())
			return null;

		return self.getTop().getGeocell().get(9);
	}


	public boolean isEmpty() {
		return que.isEmpty();
	}

	/*	public String getTopKeyWord() {
		return self.getTopWord().getKeyword().getString();
	}*/

	public Photos getTop() {
		NavigableSet<Counter> sorted = new TreeSet<EventQueue.Counter>();
		if (counterMap.isEmpty())
			return null;
		sorted.addAll(counterMap.values());
		Counter first = sorted.first();
		return convert(first);
	}

	/*	public Keywordtemp getTopWord() {
		System.out.println("getTopWord()");
		NavigableSet<Keywordtemp> sorted = new TreeSet<Keywordtemp>();
		if (wordCounterMap.isEmpty())
			return null;
		sorted.addAll(wordCounterMap.values());
		Keywordtemp first = sorted.first();
		return first;
	}*/

	public int size() {
		return que.size();
	}

	private Item convert(Photos photo) {
		Item item = null;
		//KeywordList tmp = HotKeyWord.get().ExtractKeyword(photo.getRecord());
		try {
			item = new Item(photo.getPictureID(), photo.getGeocell().get(
					GEO_CELL_IDX), photo.getOwnerId(), DATE_FORMAT.parse(photo
							.getMakeTimeS()));
		} catch (ParseException e) {
			throw new RuntimeException(String.format(
					"date time expression error %s", photo.getMakeTimeS()));
		}
		return item;
	}

	private Photos convert(Counter item) {
		Photos photos = new Photos();
		photos.setGeocell(Arrays.asList(item.getGeocell(), item.getGeocell(),
				item.getGeocell(), item.getGeocell(), item.getGeocell(),
				item.getGeocell(), item.getGeocell(), item.getGeocell(),
				item.getGeocell(), item.getGeocell(), item.getGeocell(),
				item.getGeocell(), item.getGeocell()));
		return photos;
	}

	@SuppressWarnings("unused")
	private Photos convert(Item item) {
		Photos photos = new Photos();
		photos.setGeocell(Arrays.asList(item.getGeocell(), item.getGeocell(),
				item.getGeocell(), item.getGeocell(), item.getGeocell(),
				item.getGeocell(), item.getGeocell(), item.getGeocell(),
				item.getGeocell(), item.getGeocell(), item.getGeocell(),
				item.getGeocell(), item.getGeocell()));
		photos.setMakeTimeS(DATE_FORMAT.format(item.getDateTime()));
		photos.setOwnerId(item.getOwner());
		return photos;
	}


	private void IncreaseCoolMap(Counter temp){
		Counter counter = new Counter(temp);
		if (counterMap.containsKey(counter.getGeocell())) {
			counter = counterMap.get(counter.getGeocell());
			counter.increase();
			counterMap.remove(counter.getGeocell());
			counterMap.put(counter.getGeocell(), counter);
		} else {
			counter = new Counter(counter);
			counterMap.put(counter.getGeocell(), counter);
		}
	}
	private void rotate() {
		System.out.println("rotate()");
		Calendar now = Calendar.getInstance();
		now.roll(Calendar.HOUR, -1);

		Date baseTime = now.getTime();
		Iterator<Item> iter = que.iterator();
		while (iter.hasNext()) {
			Item item = iter.next();
			if (item.getDateTime().before(baseTime)) {
				System.out.println("remove()");
				iter.remove();
				que.remove(item);
				if (counterMap.containsKey(item.getGeocell())) {
					Counter counter = counterMap.get(item.getGeocell());
					//IncreaseCoolMap(counter);
					if (counter.decrease() == 0) {
						counterMap.remove(item.getGeocell());
					} else {
						counterMap.remove(item.getGeocell());
						counterMap.put(item.getGeocell(), counter);
					}
				}
			}
		}
	}
}

/*				for (Keyword word : item.getKeywordList()) {
if (wordCounterMap.containsKey(word.getString())) {

	Keywordtemp temp = wordCounterMap.get(word.getString());
	temp.getKeyword().setCnt(
			temp.getKeyword().getCnt() - word.getCnt());
	if (temp.getKeyword().getCnt() <= 0)
		wordCounterMap.remove(word.getString());
	else {
		wordCounterMap.remove(word.getString());
		wordCounterMap.put(temp.getKeyword().getString(),
				temp);
	}
}
}*/
