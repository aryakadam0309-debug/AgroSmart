package com.agrosmart.repository;

import android.content.Context;

import com.agrosmart.database.AgroSmartDbHelper;
import com.agrosmart.models.NewsItem;

import java.util.ArrayList;
import java.util.List;

public class AgriNewsRepository {

    private static AgriNewsRepository instance;
    private final List<NewsItem> sampleNews = new ArrayList<>();
    private AgroSmartDbHelper dbHelper;

    public static synchronized AgriNewsRepository getInstance(Context context) {
        if (instance == null) {
            instance = new AgriNewsRepository(context != null ? context.getApplicationContext() : null);
        } else if (instance.dbHelper == null && context != null) {
            instance.initDb(context.getApplicationContext());
        }
        return instance;
    }

    public static synchronized AgriNewsRepository getInstance() {
        if (instance == null) {
            instance = new AgriNewsRepository(null);
        }
        return instance;
    }

    public AgriNewsRepository() {
        this(null);
    }

    public AgriNewsRepository(Context context) {
        if (context != null) {
            initDb(context);
        } else {
            populateVerifiedNews();
        }
    }

    private synchronized void initDb(Context context) {
        if (this.dbHelper == null && context != null) {
            this.dbHelper = AgroSmartDbHelper.getInstance(context);
            List<NewsItem> dbNews = dbHelper.getAllNewsFromDb();
            if (dbNews != null && !dbNews.isEmpty()) {
                sampleNews.clear();
                sampleNews.addAll(dbNews);
            } else {
                if (sampleNews.isEmpty()) {
                    populateVerifiedNews();
                }
                for (NewsItem n : sampleNews) {
                    dbHelper.insertNews(n);
                }
            }
        }
    }

    private void populateVerifiedNews() {
        sampleNews.add(new NewsItem(
                1L,
                "New Subsidies Announced for Solar Agricultural Pumps in Maharashtra",
                "State government expands solar water pump scheme for smallholder farmers with up to 90% subsidy under the PM-KUSUM initiative.",
                "The Department of Agriculture has officially announced an expansion of the solar irrigation initiative. Farmers owning between 1 to 5 acres of agricultural land can apply for solar pumps ranging from 3 HP to 7.5 HP capacity with substantial subsidy support. Applications are being routed through the official Mahakrishi portal with simplified document requirements.",
                "Government",
                "https://images.unsplash.com/photo-1509391365360-2e959784a276?w=600",
                "Ministry of Agriculture",
                "10 Sep 2026"
        ));

        sampleNews.add(new NewsItem(
                2L,
                "Soybean and Cotton Market Rates Surge in Western APMC Mandis",
                "Demand uptick in international edible oils and textile mills leads to higher farmgate prices across major trading centers.",
                "Mandi arrivals across Vidarbha and Marathwada recorded a 12% price appreciation for Grade-A non-GMO soybean and medium-staple cotton. Agricultural analysts advise farmers to stagger their market arrivals over the coming fortnights to capitalize on prevailing wholesale prices.",
                "Markets",
                "https://images.unsplash.com/photo-1595974482597-4b8da8879bc5?w=600",
                "Agri Market Watch",
                "09 Sep 2026"
        ));

        sampleNews.add(new NewsItem(
                3L,
                "Integrated Pest Management Guidelines for Kharif Paddy",
                "Agricultural universities issue timely advisory for stem borer and blast prevention following recent erratic monsoon showers.",
                "Due to intermittent humidity and temperature spikes, paddy growers in coastal and plateau regions are advised to deploy pheromone traps and neem-based bio-pesticides at the active tillering stage. Excessive nitrogenous fertilizer application should be avoided during overcast conditions.",
                "Crops",
                "https://images.unsplash.com/photo-1536657464919-892534f60d6e?w=600",
                "ICAR Advisory",
                "08 Sep 2026"
        ));

        sampleNews.add(new NewsItem(
                4L,
                "Micro-Irrigation Adoption Boosts Sugarcane Yields by 25%",
                "Field studies demonstrate that subsurface drip irrigation saves over 40% water while significantly improving sugar recovery ratios.",
                "Precision drip irrigation trials conducted in Western India demonstrate that scheduled fertigation directly to root zones reduces weed growth and maximizes fertilizer efficiency. Agronomists recommend adopting automated drip systems with state subsidy support.",
                "Agri-Tech",
                "https://images.unsplash.com/photo-1625246333195-78d9c38ad449?w=600",
                "Krishi Vigyan Kendra",
                "06 Sep 2026"
        ));
    }

    public interface NewsCallback {
        void onSuccess(List<NewsItem> newsList);
        void onError(String message);
    }

    public void getAllNews(NewsCallback callback) {
        callback.onSuccess(new ArrayList<>(sampleNews));
    }

    public void getNewsByCategory(String category, NewsCallback callback) {
        if (category == null || category.equalsIgnoreCase("All")) {
            callback.onSuccess(new ArrayList<>(sampleNews));
            return;
        }
        List<NewsItem> filtered = new ArrayList<>();
        for (NewsItem item : sampleNews) {
            if (category.equalsIgnoreCase(item.getCategory())) {
                filtered.add(item);
            }
        }
        callback.onSuccess(filtered);
    }

    public void addNews(NewsItem item) {
        sampleNews.add(0, item);
        if (dbHelper != null) {
            dbHelper.insertNews(item);
        }
    }

    public boolean removeNews(long id) {
        if (dbHelper != null) {
            dbHelper.deleteNews(id);
        }
        for (int i = 0; i < sampleNews.size(); i++) {
            if (sampleNews.get(i).getId() == id) {
                sampleNews.remove(i);
                return true;
            }
        }
        return false;
    }

    public List<NewsItem> getAllNewsList() {
        return new ArrayList<>(sampleNews);
    }
}
