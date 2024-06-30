package com.example.hottour;
import com.dpcsa.compon.base.DeclareScreens;
import com.dpcsa.compon.interfaces_classes.Menu;
import com.dpcsa.compon.param.ParamMap;

public class MyDeclare extends DeclareScreens {

    public final static String
            MAIN = "MAIN", SEARCH = "SEARCH", HANTER = "HANTER",
            MY_TOUR = "MY_TOUR", HELP = "HELP", HOT_INSIDE = "HOT_INSIDE",
            HOT_DEPART_CITY = "HOT_DEPART_CITY", DEPART_CITY = "DEPART_CITY", COUNTRY_CITY = "COUNTRY_CITY",
            SELECT_TOURS = "SELECT_TOURS", CITY = "CITY", SEARCH_D_D = "SEARCH_D_D",
            SEL_DATE = "SEL_DATE", SEL_LEN = "SEL_LEN", WHO_FLYING = "WHO_FLYING",
            DRAWER = "DRAWER", MAP = "MAP", ORDER = "ORDER",
            SERVICE = "SERVICE";

    @Override
    public void declare() {

        activity(MAIN, R.layout.activity_main)
                .menuBottom(model(menuMainMenu_b), view(R.id.menu_b))
                .drawer(R.id.drawer, R.id.container_fragm, R.id.left_drawer, null, DRAWER);

        fragment(SEARCH, R.layout.fragment_search, R.string.main_menu_b_Search)
                .navigator(start(R.id.country,COUNTRY_CITY, after(setVar(R.id.country_city,"country_city","country_name,city_name"))))
                .menuBottom(model(menuSearchMenu_b), view(R.id.menu_b),
                        navigator(hide(R.id.sheet_h),show(R.id.sheet_s)),
                        navigator(hide(R.id.sheet_s),show(R.id.sheet_h)))
                .component(TC.PANEL, model(PARAMETERS, "id_depart_cities=2"),
                        view(R.id.panel),
                        navigator(start(HOT_DEPART_CITY, after(setValueParam(R.id.panel),
                                actual(R.id.list)))))
                .list(model("query/zyzdpi8g7csjov5/5", "id_depart_cities=2"),
                        view(R.id.list, R.layout.item_search_list_0).spanCount(2),
                        navigator(start(HOT_INSIDE)))
                .component(TC.PANEL_ENTER, model(PARAMETERS, "depart_city_name=Алматы,depart_date=SysDate,adults=1,kids=0,country_name=Египет"),
                        view(R.id.form),
                        navigator(start(R.id.depart,DEPART_CITY, after(setValueParam(R.id.depart))),
                                start(R.id.select,SELECT_TOURS, false, R.id.country_city),
                                start(R.id.date,SEARCH_D_D, after(assignValue(R.id.date))),
                                start(R.id.who,WHO_FLYING, after(assignValue(R.id.who)))));

        fragment(HANTER, R.layout.fragment_hanter, R.string.main_menu_b_Hanter)
                .navigator(springY(R.id.hhh, R.id.rrr, -150, 3, 0));


        fragment(MY_TOUR, R.layout.fragment_my_tour, R.string.main_menu_b_My_tour);
        fragment(HELP, R.layout.fragment_help, R.string.main_menu_b_Help);
        activity(HOT_INSIDE, R.layout.activity_hot_inside).animate(AS.RL)
                .navigator(back(R.id.back))
                .list(model(JSON, "[{\"id_field\":1,\"img\":\"https://ht.kz/fls/post/1578308176bfab8958.jpg\",\"discont\":\"-33%\",\"no_time\":\"Осталось 3 дня\",\"cost\":\"от 123 345 Т\",\"title\":\"Лучшие отели Пхукета\\\\nна первой линии!\",\"txt\":\"Предлагаем Вашему вниманию ТОП лучших отелей находящихся на первой линии!\\\\nОтели с прекрасным сервисом и прямым доступом к морю! Виза оформляется по прилету!\"},{\"id_field\":2,\"img\":\"https://ht.kz/fls/post/15753685459eb50ca8.jpg\",\"discont\":\"-33%\",\"no_time\":\"Осталось 4 дня\",\"cost\":\"от 123 345 Т\",\"title\":\"Новинка! Маврикий\\\\nиз Алматы!\",\"txt\":\"Маврикий — звезда Индийского океана!\\\\nПляжи с белоснежным песком, разнообразный подводный мир, а так же все возможности для морской рыбалки!\"}]"),
                        view(R.id.list, R.layout.item_hot_inside_list_0));

        activity(HOT_DEPART_CITY, R.layout.activity_hot_depart_city).animate(AS.RL)
                .navigator(backOk(R.id.selected))
                .list(model("query/zyzdpi8g7csjov5/6"),
                        view(R.id.list, "type", new int[]{R.layout.item_hot_depart_city_list_0,
                                R.layout.item_hot_depart_city_list_1,
                                R.layout.item_hot_depart_city_list_2}).selected("id_depart_cities=2", TVS.PARAM),
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
                        view(R.id.list, R.layout.item_country_city_list_0),
                        navigator(addVar(R.id.selcity, "country_city", "country_id,country_name"),
                                addVar(R.id.T_0, "country_city", "country_id,country_name"),
                                delVarFollow(R.id.T_0, "country_city", "country_id,country_name"),
                                backOk(R.id.T_0),
                                start(R.id.selcity,CITY, after(backOk()))));

        activity(SELECT_TOURS, R.layout.activity_select_tours)
                .setValue(                setParam(R.id.marsh, "country_name,depart_city_name"))
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
                        new String[] {SEL_DATE,SEL_LEN})
                        .setTab(R.id.tab_layout, R.array.search_d_d_tab_layout));

        fragment(SEL_DATE, R.layout.fragment_sel_date);
        fragment(SEL_LEN, R.layout.fragment_sel_len)
                .navigator(backOk(R.id.select, "depart_date,night"));
        activity(WHO_FLYING, R.layout.activity_who_flying)
                .setValue(                setParam(R.id.plus_minus, "adults")                ,
                        set(R.id.kids, TS.SIZE, R.id.list))
                .navigator(show(R.id.add_k, R.id.tags),
                        back(R.id.back),
                        backOk(R.id.ok, "kids,adults"),
                        backOk(R.id.sel, "kids,adults"),
                        hide(R.id.add_k, R.id.add_k))
                .component(TC.TAGS, model(JSON, "[{\"id_field\":1,\"year\":\"0\",\"title\":\"до года\"},{\"id_field\":2,\"year\":\"1\",\"title\":\"1\"},{\"id_field\":3,\"year\":\"2\",\"title\":\"2\"},{\"id_field\":4,\"year\":\"3\",\"title\":\"3\"},{\"id_field\":5,\"year\":\"4\",\"title\":\"4\"},{\"id_field\":6,\"year\":\"5\",\"title\":\"5\"},{\"id_field\":7,\"year\":\"6\",\"title\":\"6\"},{\"id_field\":8,\"year\":\"7\",\"title\":\"7\"},{\"id_field\":9,\"year\":\"8\",\"title\":\"8\"},{\"id_field\":10,\"year\":\"9\",\"title\":\"9\"},{\"id_field\":11,\"year\":\"10\",\"title\":\"10\"},{\"id_field\":12,\"year\":\"11\",\"title\":\"11\"},{\"id_field\":13,\"year\":\"12\",\"title\":\"12\"},{\"id_field\":14,\"year\":\"13\",\"title\":\"13\"},{\"id_field\":15,\"year\":\"14\",\"title\":\"14\"},{\"id_field\":16,\"year\":\"15\",\"title\":\"15\"},{\"id_field\":17,\"year\":\"16\",\"title\":\"16\"}]"),
                        view(R.id.tags, R.layout.item_who_flying_tags_0),
                        navigator(handler(0, VH.ADD_ITEM_LIST, R.id.list),
                                hide(R.id.tags),
                                show(R.id.add_k)))
                .list(model(PARAMETERS, "kids_gl"),
                        view(R.id.list, R.layout.item_who_flying_list_0),
                        navigator(handler(R.id.del_kind, VH.DEL_ITEM_LIST)))
                .plusMinus(R.id.plus_minus, R.id.plus, R.id.minus, null, null);

        fragment(DRAWER, R.layout.fragment_drawer)
                .menu(model(menu_Drawer_Menu), view(R.id.menu));

        fragment(MAP, R.layout.fragment_map).animate(AS.RL)
                .componentMap(R.id.map, model("services/markers"),
                        new ParamMap(true).levelZoom(5f)
                                .coordinateValue(48.3794327,31.1655807)
                                .markerImg(0, R.drawable.pin)
                                .markerClick(R.id.mark_map, false)
                                .mapControls(true,true),
                        navigator(start(R.id.butt,ORDER),
                                handler(R.id.phones, VH.DIAL_UP)));

        activity(ORDER, R.layout.activity_order);
        fragment(SERVICE, R.layout.fragment_service)
                .startNavigator(springScale(R.id.img_r, 3, 1000))
                .navigator(start(R.id.sel,MAP));
    }

    Menu menuMainMenu_b = new Menu()
            .item(R.drawable.search, R.string.main_menu_b_Search, SEARCH, true)
            .item(R.drawable.tourhunter_menu, R.string.main_menu_b_Hanter, HANTER)
            .item(R.drawable.my_tour, R.string.main_menu_b_My_tour, MY_TOUR)
            .item(R.drawable.help, R.string.main_menu_b_Help, HELP);
    Menu menuSearchMenu_b = new Menu()
            .item(0, R.string.search_menu_b_0, "", true)
            .item(R.drawable.fire_20, R.string.search_menu_b_1, "");
    Menu menu_Drawer_Menu = new Menu()
            .item(R.drawable.search, R.string.drawer_menu_Search, SEARCH, true)
            .item(R.drawable.hist_gray, R.string.drawer_menu_Service, SERVICE);
}
