package com.example.ide;
import com.dpcsa.compon.base.DeclareScreens;
import com.dpcsa.compon.interfaces_classes.Menu;

public class MyDeclare extends DeclareScreens {

    public final static String
            MAIN = "MAIN", SEARCH = "SEARCH", HANTER = "HANTER",
            MY_TOUR = "MY_TOUR", HELP = "HELP", HOT_INSIDE = "HOT_INSIDE",
            HOT_DEPART_CITY = "HOT_DEPART_CITY", DEPART_CITY = "DEPART_CITY", COUNTRY_CITY = "COUNTRY_CITY",
            SELECT_TOURS = "SELECT_TOURS", CITY = "CITY", SEARCH_D_D = "SEARCH_D_D",
            SEL_DATE = "SEL_DATE", SEL_LEN = "SEL_LEN", WHO_FLYING = "WHO_FLYING";

    @Override
    public void declare() {

        activity(MAIN, R.layout.activity_main)
                .fragmentsContainer(R.id.container_fragm)
                .menuBottom(model(menuMainMenu_b), view(R.id.menu_b));

        fragment(SEARCH, R.layout.fragment_search, R.string.search_screen_title)
                .navigator(start(R.id.country,COUNTRY_CITY, after(setVar(R.id.country_city,"country_city","country_name,city_name"))))
                .menuBottom(model(menuSearchMenu_b), view(R.id.menu_b),
                        navigator(hide(R.id.sheet_h),show(R.id.sheet_s)),
                        navigator(hide(R.id.sheet_s),show(R.id.sheet_h)))
                .component(TC.PANEL, model(PARAMETERS, "hot_flag_country,hot_depart_city_name"),
                        view(R.id.panel),
                        navigator(start(HOT_DEPART_CITY, after(setValueParam(R.id.panel),
                                actual(R.id.list)))))
                .list(model("htkzhot/hot_tour", "hot_depart_city_id=2"),
                        view(R.id.list, R.layout.item_search_list_0).spanCount(2),
                        navigator(start(HOT_INSIDE)))
                .component(TC.PANEL_ENTER, model(PARAMETERS, "depart_city_name=Алматы,depart_date=SysDate,adults=1,kids=0"),
                        view(R.id.form),
                        navigator(start(R.id.depart,DEPART_CITY, after(setValueParam(R.id.depart))),
                                start(R.id.select,SELECT_TOURS, false, R.id.country_city),
                                start(R.id.date,SEARCH_D_D, after(assignValue(R.id.date))),
                                start(R.id.who,WHO_FLYING, after(assignValue(R.id.who)))));

        fragment(HANTER, R.layout.fragment_hanter, R.string.hanter_screen_title);
        fragment(MY_TOUR, R.layout.fragment_my_tour, R.string.my_tour_screen_title);
        fragment(HELP, R.layout.fragment_help, R.string.help_screen_title);
        activity(HOT_INSIDE, R.layout.activity_hot_inside).animate(AS.RL)
                .navigator(back(R.id.back))
                .list(model(JSON, "[{\"img\":\"https://ht.kz/fls/post/1578308176bfab8958.jpg\",\"discont\":\"-33%\",\"no_time\":\"Осталось 3 дня\",\"cost\":\"от 123 345 Т\",\"title\":\"Лучшие отели Пхукета\\\\nна первой линии!\",\"txt\":\"Предлагаем Вашему вниманию ТОП лучших отелей находящихся на первой линии!\\\\nОтели с прекрасным сервисом и прямым доступом к морю! Виза оформляется по прилету!\"},{\"img\":\"https://ht.kz/fls/post/15753685459eb50ca8.jpg\",\"discont\":\"-33%\",\"no_time\":\"Осталось 4 дня\",\"cost\":\"от 123 345 Т\",\"title\":\"Новинка! Маврикий\\\\nиз Алматы!\",\"txt\":\"Маврикий — звезда Индийского океана!\\\\nПляжи с белоснежным песком, разнообразный подводный мир, а так же все возможности для морской рыбалки!\"}]"),
                        view(R.id.list, R.layout.item_hot_inside_list_0));

        activity(HOT_DEPART_CITY, R.layout.activity_hot_depart_city).animate(AS.RL)
                .navigator(backOk(R.id.selected))
                .list(model("htkzhot/hot_depart_city"),
                        view(R.id.list, "sel", new int[]{R.layout.item_hot_depart_city_list_0,
                                R.layout.item_hot_depart_city_list_1,
                                R.layout.item_hot_depart_city_list_2}).selected("hot_depart_city_id=2", TVS.PARAM),
                        navigator(show(R.id.selected)));

        activity(DEPART_CITY, R.layout.activity_depart_city).animate(AS.RL)
                .navigator(backOk(R.id.selected))
                .list(model("htkzhot/depart_city"),
                        view(R.id.list, "sel", new int[]{R.layout.item_depart_city_list_0,
                                R.layout.item_depart_city_list_1,
                                R.layout.item_depart_city_list_2}).selected("depart_city_id=2", TVS.PARAM),
                        navigator(show(R.id.selected)));

        activity(COUNTRY_CITY, R.layout.activity_country_city).animate(AS.RL)
                .startNavigator(cleanCopyVar("country_city"))
                .navigator(restoreVar(R.id.back, "country_city"),
                        backOk(R.id.back))
                .list(model("htkzhot/country"),
                        view(R.id.list, R.layout.item_country_city_list_0).visibilityManager(visibility(R.id.visa,"visa")),
                        navigator(addVar(R.id.selcity, "country_city", "country_id,country_name"),
                                addVar(R.id.T_0, "country_city", "country_id,country_name"),
                                delVarFollow(R.id.T_0, "country_city", "country_id,country_name"),
                                backOk(R.id.T_0),
                                start(R.id.selcity,CITY, after(backOk()))));

        activity(SELECT_TOURS, R.layout.activity_select_tours)
                .setValue(setParam(R.id.marsh, "country_name,depart_city_name"))
                .list(model(POST, "htkzhot/find_tours", "kids,adults,depart_date,night,depart_city_id,country_city(country_id;city_id)"),
                        view(R.id.list, R.layout.item_select_tours_list_0));

        activity(CITY, R.layout.activity_city)
                .navigator(back(R.id.back))
                .list(model("htkzhot/city", "country_id"),
                        view(R.id.list, R.layout.item_city_list_0),
                        navigator(addVar(R.id.T_0, "country_city", "city_id,city_name"),
                                delVarFollow(R.id.T_0, "country_city", "city_id,city_name"),
                                backOk(R.id.T_0)));

        activity(SEARCH_D_D, R.layout.activity_search_d_d)
                .navigator(back(R.id.back),
                        backOk(R.id.ok, "depart_date,night"))
                .component(TC.PAGER_F, view(R.id.pager,
                        new String[] {SEL_DATE, SEL_LEN})
                        .setTab(R.id.tab_layout, R.array.search_d_d_tab_layout));

        fragment(SEL_DATE, R.layout.fragment_sel_date);
        fragment(SEL_LEN, R.layout.fragment_sel_len)
                .navigator(backOk(R.id.select, "depart_date,night"));
        activity(WHO_FLYING, R.layout.activity_who_flying)
                .setValue(setParam(R.id.plus_minus, "adults"),
                        set(R.id.kids, TS.SIZE, R.id.list))
                .navigator(show(R.id.add_k, R.id.tags),
                        back(R.id.back),
                        backOk(R.id.ok, "kids,adults"),
                        backOk(R.id.sel, "kids,adults"),
                        hide(R.id.add_k, R.id.add_k))
                .component(TC.TAGS, model(JSON, "[{\"year\":\"0\",\"title\":\"до года\"},{\"year\":\"1\",\"title\":\"1\"},{\"year\":\"2\",\"title\":\"2\"},{\"year\":\"3\",\"title\":\"3\"},{\"year\":\"4\",\"title\":\"4\"},{\"year\":\"5\",\"title\":\"5\"},{\"year\":\"6\",\"title\":\"6\"},{\"year\":\"7\",\"title\":\"7\"},{\"year\":\"8\",\"title\":\"8\"},{\"year\":\"9\",\"title\":\"9\"},{\"year\":\"10\",\"title\":\"10\"},{\"year\":\"11\",\"title\":\"11\"},{\"year\":\"12\",\"title\":\"12\"},{\"year\":\"13\",\"title\":\"13\"},{\"year\":\"14\",\"title\":\"14\"},{\"year\":\"15\",\"title\":\"15\"},{\"year\":\"16\",\"title\":\"16\"}]"),
                        view(R.id.tags, R.layout.item_who_flying_tags_0),
                        navigator(handler(0, VH.ADD_RECORD, R.id.list),
                                hide(R.id.tags),
                                show(R.id.add_k)))
                .list(model(GLOBAL, "kids_gl"),
                        view(R.id.list, R.layout.item_who_flying_list_0),
                        navigator(handler(R.id.del_kind, VH.DEL_RECORD)))
                .plusMinus(R.id.plus_minus, R.id.plus, R.id.minus);

    }

    Menu menuMainMenu_b = new Menu()
            .item(R.drawable.search, R.string.main_menu_b_0, SEARCH, true)
            .item(R.drawable.tourhunter_menu, R.string.main_menu_b_1, HANTER)
            .item(R.drawable.my_tour, R.string.main_menu_b_2, MY_TOUR)
            .item(R.drawable.help, R.string.main_menu_b_3, HELP);
    Menu menuSearchMenu_b = new Menu()
            .item(0, R.string.search_menu_b_0, "", true)
            .item(R.drawable.fire_20, R.string.search_menu_b_1, "");

}
