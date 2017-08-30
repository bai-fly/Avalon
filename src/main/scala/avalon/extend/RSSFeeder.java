package avalon.extend;

import avalon.tool.pool.ConstantPool;
import avalon.tool.system.ConfigSystem;
import avalon.tool.system.GroupConfigSystem;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

public class RSSFeeder implements Runnable {
	private List<URL> urls = toURLList(ConfigSystem.getInstance().getCommandConfigArray("RSS", "feed"));
	private Map<URL, LocalDateTime> updated = new HashMap<>();
	private static RSSFeeder instance = null;

	public static RSSFeeder getInstance() {
		if (instance == null) instance = new RSSFeeder();
		return instance;
	}

	private RSSFeeder() {
		for (URL thisURL : urls)
			updated.put(thisURL, LocalDateTime.MIN);
	}

	private RSSParser.RSSItem update() {
		Map<URL, List<RSSParser.RSSItem>> map = getAllItemsMap();
		List<RSSParser.RSSItem> result = new ArrayList<>();
		for (Map.Entry<URL, List<RSSParser.RSSItem>> entry : map.entrySet()) {
			URL key = entry.getKey();
			List<RSSParser.RSSItem> value = entry.getValue();
			for (RSSParser.RSSItem thisItem : value)
				if (updated.containsKey(key) && !value.isEmpty() && thisItem.pubDate().isAfter(updated.get(key))) {
					result.add(thisItem);
					updated.replace(key, thisItem.pubDate());
				}
		}
		return result.isEmpty() ? null : result.get(new Random().nextInt(result.size()));
	}

	private Map<URL, List<RSSParser.RSSItem>> getAllItemsMap() {
		Map<URL, List<RSSParser.RSSItem>> result = new HashMap<>();
		for (URL thisURL : urls)
			result.put(thisURL, RSSParser.get(thisURL));
		return result;
	}

	private static List<URL> toURLList(Object[] in) {
		List<URL> result = new ArrayList<>();
		for (Object anIn : in)
			if (anIn instanceof String)
				try {
					result.add(new URL((String) anIn));
				} catch (MalformedURLException e) {
					throw new RuntimeException(e);
				}
		return result;
	}

	@Override
	public void run() {
		RSSParser.RSSItem newest = update();
		if (newest != null) {
			for (long groupUid : GroupConfigSystem.instance().getFollowGroups())
				ConstantPool.Basic.currentServlet.responseGroup(groupUid,
						String.format("订阅的RSS %s - %s 有更新：\n%s\n发布时间：%s 详见：%s",
								newest.info().title(),
								newest.info().description(),
								newest.title(),
								newest.pubDate().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)),
								newest.link()));
		}
	}
}
