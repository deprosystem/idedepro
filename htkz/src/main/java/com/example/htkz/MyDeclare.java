package com.example.htkz;

import com.dpcsa.compon.base.DeclareScreens;
import com.dpcsa.compon.interfaces_classes.Menu;
import com.example.htkz.custom.WorkWhoFlying;

import static com.dpcsa.compon.interfaces_classes.ViewHandler.TYPE.ADD_RECORD;
import static com.dpcsa.compon.interfaces_classes.ViewHandler.TYPE.DEL_RECORD;
import static com.dpcsa.compon.param.ParamComponent.TC.PAGER_V;
import static com.dpcsa.compon.param.ParamComponent.TC.TAGS;
import static com.dpcsa.compon.param.ParamView.TYPE_VALUE_SELECTED.PARAM;
import static com.dpcsa.compon.tools.Constants.AnimateScreen.BT;

public class MyDeclare extends DeclareScreens {

    public final static String
        MAIN = "MAIN", SEARCH = "SEARCH", HANTER = "HANTER", MY_TOUR = "MY_TOUR", HELP = "HELP",
            SPLASH = "SPLASH", HOT_INSIDE = "HOT_INSIDE", DEPART_CITY = "DEPART_CITY",
            HOT_DEPART_CITY = "HOT_DEPART_CITY", SEARCH_D_D = "SEARCH_D_D", WHO_FLYING = "WHO_FLYING",
            COUNTRY_CITY = "COUNTRY_CITY", CITY = "CITY", SELECT_TOURS = "SELECT_TOURS";

    @Override
    public void declare() {

        activity(SPLASH, R.layout.activity_splash).navigator()
                .autoAutch(model(GET, API.AUTO, "access-token"), null, null,
                        after(setToken("key")), MAIN);

        activity(MAIN, R.layout.activity_main)
                .navigator()
                .fragmentsContainer(R.id.container)
                .menuBottom(model(menu), view(R.id.menu));

        fragment(SEARCH, R.layout.fragment_search)
                .setValue(setParam(R.id.city_hot), setParam(R.id.city),
                        setParam(R.id.adults), setParam(R.id.kids), setGlob(R.id.country_city, "country_city"))
                .navigator(start(R.id.city_hot, HOT_DEPART_CITY, after(setValueParam(R.id.city_hot), actual(R.id.list))),
                        start(R.id.city, DEPART_CITY, after(setValueParam(R.id.city))),
                        start(R.id.country, COUNTRY_CITY, after(setVar(R.id.country_city, "country_city"))),
                        start(R.id.date, SEARCH_D_D, after(assignValue(R.id.date))),
                        start(R.id.who, WHO_FLYING, after(assignValue(R.id.who_flying))))
                .menuBottom(model(menuSearch), view(R.id.menu_b), navigator(hide(R.id.hot_t), show(R.id.search_t)),
                        navigator(hide(R.id.search_t), show(R.id.hot_t)))
                .component(TC.PANEL_ENTER, null,
                        view(R.id.panel), navigator(start(R.id.select, SELECT_TOURS, false, R.id.country_city)))
                .list(model(API.HOT_TOUR, "hot_depart_city_id").progress(R.id.progress_hot),
                        view(R.id.list, R.layout.item_hot_t).spanCount(2),
                        navigator(start(HOT_INSIDE)));

        fragment(HANTER, R.layout.fragment_hanter).startNavigator(springScale(R.id.hant_img, 3, 1000));

        fragment(MY_TOUR, R.layout.fragment_my_tour)
                .startNavigator(springY(R.id.hant_img, -1000, 1000));

        fragment(HELP, R.layout.fragment_help)
                .list(model(JSON, "[]"), view(R.id.recycler, R.layout.item_kind),
                        navigator(handler(R.id.del_kind, DEL_RECORD)))
                .component(TAGS, model(JSON, getString(R.string.age)),
                        view(R.id.age, R.layout.item_age),
                        navigator(handler(0, ADD_RECORD, R.id.recycler)));

        activity(SELECT_TOURS, R.layout.activity_select_tour)
                .setValue(setParam(R.id.marsh))
                .list(model(POST, API.FIND_TOURS,
                        "kids,adults,depart_date,night,depart_city_id,country_city(country_id;city_id)"),
                        view(R.id.list, R.layout.item_select_tour));

        activity(COUNTRY_CITY, R.layout.activity_country_city)
                .startNavigator(cleanCopyVar("country_city"))
                .navigator(restoreVar(R.id.back, "country_city"), backOk(R.id.back))
                .list(model(API.COUNTRY),
                        view(R.id.recycler, "type", new int[] {R.layout.item_country_city, R.layout.item_city})
                                .visibilityManager(visibility(R.id.visa, "visa")),
                        navigator(addVar(R.id.selcity, "country_city", "country_id,country_name"),
                                start(R.id.selcity, CITY, after(backOk())),
                                addVar(R.id.country, "country_city", "country_id,country_name"),
                                delVar(R.id.country, "country_city", "city_id,city_name"),
                                addVar(R.id.city, "country_city", "country_id,country_name,city_id,city_name"),
                                backOk(R.id.country), backOk(R.id.city)))
                .componentSearch(R.id.search_t, model(API.SEARCH_COUNTRY_CITY, "search-c-c"), R.id.recycler);

        activity(CITY, R.layout.activity_city)
                .list(model(API.CITY, "country_id"),
                        view(R.id.recycler, R.layout.item_city),
                        navigator(addVar(R.id.city, "country_city", "city_id,city_name"), backOk(R.id.city)));

        activity(WHO_FLYING, R.layout.activity_who_flying, WorkWhoFlying.class)
                .setValue(setParam(R.id.amount, "adults"))
                .navigator(show(R.id.add_kid, R.id.kid_age), hide(R.id.cancel, R.id.kid_age),
                        backOk(R.id.select, "kids,adults"),
                        back(R.id.back), backOk(R.id.ok, "kids,adults"))
                .plusMinus(R.id.amount, R.id.plus, R.id.minus, null, null)
                .list(model(GLOBAL, "kids_gl"), view(R.id.recycler, R.layout.item_kind),
                        navigator(handler(R.id.del_kind, DEL_RECORD)))
                .component(TAGS, model(JSON, getString(R.string.age)),
                        view(R.id.age, R.layout.item_age),
                        navigator(handler(0, ADD_RECORD, R.id.recycler), hide(R.id.kid_age)));

        activity(SEARCH_D_D, R.layout.activity_search_d_d)
                .navigator(back(R.id.back), backOk(R.id.ok, "depart_date,night"),
                        backOk(R.id.select, "depart_date,night"))
                .component(PAGER_V, null,
                        view(R.id.pager, new int[] {R.layout.view_search_dd_1, R.layout.view_search_dd_2})
                                .setTab(R.id.tabs, R.array.dd),
                        navigator(backOk(R.id.select, "depart_date,night"), setValue(R.id.n_5, R.id.seek, "5"),
                                setValue(R.id.n_7, R.id.seek, "7"), setValue(R.id.n_9, R.id.seek, "9"),
                                setValue(R.id.n_11, R.id.seek, "11"), setValue(R.id.n_14, R.id.seek, "14")));

        activity(HOT_INSIDE, R.layout.fragment_hot_inside).animate(BT)
                .setValue(setParam(R.id.marsh))
                .navigator(back(R.id.back))
                .list(model(JSON, getString(R.string.ins_test)), view(R.id.list, R.layout.item_hot_inside));

        activity(HOT_DEPART_CITY, R.layout.activity_departure_city)
                .navigator(back(R.id.back), backOk(R.id.select), backOk(R.id.ok))
                .list(model(API.HOT_DEPART_CITY),
                        view(R.id.list, "sel", new int[]{R.layout.item_hot_departure_city_0,
                                R.layout.item_hot_departure_city_1, R.layout.item_hot_departure_city_2})
                                .selected("hot_depart_city_id", PARAM),
                        navigator(show(R.id.select), show(R.id.ok)));

        activity(DEPART_CITY, R.layout.activity_departure_city)
                .navigator(back(R.id.back), backOk(R.id.select), backOk(R.id.ok))
                .list(model(API.DEPART_CITY),
                        view(R.id.list, "sel", new int[]{R.layout.item_departure_city_0,
                                R.layout.item_departure_city_1, R.layout.item_departure_city_2})
                                .selected("depart_city_id", PARAM),
                        navigator(show(R.id.select), show(R.id.ok)));

    }

    Menu menuSearch = new Menu(R.color.white, R.color.black)
        .item(0, R.string.search_t, "", true)
        .item(R.drawable.fire_20, R.string.burning_t, "");

    Menu menu = new Menu()
            .item(R.drawable.search, R.string.search, "SEARCH", true)
            .item(R.drawable.tourhunter_menu, R.string.hanter, "HANTER")
            .item(R.drawable.my_tour, R.string.my_tour, "MY_TOUR")
            .item(R.drawable.help, R.string.help, "HELP");



}
