package com.example.htkz;

import com.dpcsa.compon.base.DeclareScreens;
import com.dpcsa.compon.interfaces_classes.ItemSetValue;
import com.dpcsa.compon.interfaces_classes.Menu;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.param.ParamView;

import java.util.HashMap;
import java.util.Map;

import static com.dpcsa.compon.param.ParamView.TYPE_VALUE_SELECTED.PARAM;
import static com.dpcsa.compon.tools.Constants.AnimateScreen.BT;

public class MyDeclare extends DeclareScreens {

    public final static String
        MAIN = "MAIN", SEARCH = "SEARCH", HANTER = "HANTER", MY_TOUR = "MY_TOUR", HELP = "HELP",
            SPLASH = "SPLASH", HOT_INSIDE = "HOT_INSIDE", DEPARTURE_CITY = "activity_departure_city";

    @Override
    public void declare() {

//        Map<String, String> head = new HashMap();
//        head.put("accept","application/json");
//        head.put("Content-Type","application/json");

        activity(SPLASH, R.layout.activity_splash).fullScreen().navigator()
                .autoAutch(model(GET, API.AUTO, "access-token"), null, null,
                        after(setToken("key")), MAIN);

        activity(MAIN, R.layout.activity_main).fullScreen()
                .navigator()
                .fragmentsContainer(R.id.container)
                .menuBottom(model(menu), view(R.id.menu));

        fragment(SEARCH, R.layout.fragment_search)
                .setValue(itemParam(R.id.city_hot))
                .navigator(start(R.id.city_hot, DEPARTURE_CITY, after(setValueParam(R.id.city_hot))))
                .menuBottom(model(menuSearch), view(R.id.menu_b), navigator(hide(R.id.hot_t), show(R.id.search_t)),
                        navigator(hide(R.id.search_t), show(R.id.hot_t)))
                .list(model(JSON, getString(R.string.hot_test)).progress(R.id.progress_hot),
                        view(R.id.list, R.layout.item_hot_t).spanCount(2),
                        navigator(start(HOT_INSIDE)));

        fragment(HANTER, R.layout.fragment_hanter).startNavigator(springScale(R.id.hant_img, 3, 1000));

        fragment(MY_TOUR, R.layout.fragment_my_tour)
                .startNavigator(springY(R.id.hant_img, -1000, 1000));

        activity(HOT_INSIDE, R.layout.fragment_hot_inside).fullScreen().animate(BT)
                .navigator(back(R.id.back))
                .list(model(JSON, getString(R.string.ins_test)), view(R.id.list, R.layout.item_hot_inside));

        activity(DEPARTURE_CITY, R.layout.activity_departure_city)
                .navigator(back(R.id.back), backOk(R.id.select))
                .list(model(JSON, getString(R.string.dep_city_test)), view(R.id.list, "sel", new int[]{R.layout.item_departure_city_0,
                        R.layout.item_departure_city_1, R.layout.item_departure_city_2})
                        .selected("cityId_hot", PARAM));

    }

    Menu menuSearch = new Menu(R.color.white, R.color.black)
        .item(0, R.string.search_t, "", true)
        .item(R.drawable.fire_20, R.string.burning_t, "");

    Menu menu = new Menu()
            .item(R.drawable.search, R.string.search, "SEARCH", true)
            .item(R.drawable.tourhunter_menu, R.string.hanter, "HANTER")
            .item(R.drawable.my_tour, R.string.my_tour, "MY_TOUR")
            .item(R.drawable.help, R.string.help, "HELP");

//    Record rec_auto = new Record()
//            .addField("platform", Field.TYPE_INTEGER, 1)
//            .addField("deviceName", Field.TYPE_STRING, "")
////            .addField("fcmKey", Field.TYPE_STRING, "ea7604df1394e6f1229f81c9716079d9")
//            ;


}
