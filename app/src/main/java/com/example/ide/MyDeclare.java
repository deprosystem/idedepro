package com.example.ide;

import com.dpcsa.compon.base.DeclareScreens;
import com.dpcsa.compon.interfaces_classes.Menu;
import com.dpcsa.compon.interfaces_classes.ViewHandler;

public class MyDeclare extends DeclareScreens {
    public final static String
            MAIN_1 = "MAIN_1", NEWS = "NEWS", PROMOT = "PROMOT",
            DETAIL = "DETAIL", DRAWER = "DRAWER", SELL = "SELL",
            MAIN = "MAIN", INTRO = "INTRO", AUTH = "AUTH",
            SIGNIN = "SIGNIN", SIGNUP = "SIGNUP", PROFILE = "PROFILE",
            BUY = "BUY", DETAIL_PROD = "DETAIL_PROD", DETAIL_USER = "DETAIL_USER",
            MYADS = "MYADS", DETAIL_MY = "DETAIL_MY", EMALATOR = "EMALATOR";

    @Override
    public void declare() {

        activity(MAIN_1, R.layout.activity_main_1)
                .component(TC.TOOL, null, view(R.id.tool_bar, new int[] {R.drawable.nar_light, R.drawable._menu_ffffffff, R.drawable._more_vert_ffffffff}).setBooleanParam(true))
                .menuBottom(model(menuMain_1Menu_b), view(R.id.menu_b))
                .drawer(R.id.drawer, R.id.container_fragm, R.id.left_drawer, null, DRAWER);

        fragment(NEWS, R.layout.fragment_news, R.string.news_screen_title)
                .list(model("query/czeux6pkp1l73gb/4"),
                        view(R.id.list, R.layout.item_news_list_0),
                        navigator(start(DETAIL)));

        fragment(PROMOT, R.layout.fragment_promot, R.string.promot_screen_title)
                .list(model("query/czeux6pkp1l73gb/38"),
                        view(R.id.list, R.layout.item_promot_list_0));

        fragment(DETAIL, R.layout.activity_detail)
                .component(TC.PANEL, model("query/czeux6pkp1l73gb/5", "id_news"),
                        view(R.id.scroll_panel));

        fragment(DRAWER, R.layout.fragment_drawer)
                .navigator(start(R.id.enter,AUTH))
                .menu(model(menu_Drawer_Menu), view(R.id.menu))
                .component(TC.PANEL, model(PROFILE),
                        view(R.id.panel).noDataView(R.id.no_v),
                        navigator(exit(R.id.ex),
                                handler(R.id.ex, VH.SET_MENU_ITEM, NEWS)));

        fragment(SELL, R.layout.fragment_sell, R.string.sell_screen_title)
                .navigator(handler(R.id.clear, VH.CLEAR_FORM, R.id.scroll_form, "img,title,mileage,praice,year,model,marka"),
                        send(R.id.send, R.id.scroll_form, model(POST, "query/czeux6pkp1l73gb/20", "img,title,id_user=Profile,name_marka,name_model,year,mileage,praice,gal")
                                , after(),
                                false, R.id.title))
                .component(TC.PANEL_ENTER, null,
                        view(R.id.scroll_form))
                .component(TC.SPINNER, model("query/czeux6pkp1l73gb/8", 90000000),
                        view(R.id.marka, R.layout.item_sell_marka_drop, R.layout.item_sell_marka_header),
                        navigator(handler(0, VH.ACTUAL, R.id.model).setTypeEvent(ViewHandler.evCHANGE)))
                .component(TC.SPINNER, model("query/czeux6pkp1l73gb/9", "id_marka"),
                        view(R.id.model, R.layout.item_sell_model_drop, R.layout.item_sell_model_header)).noActualStart()
                .component(TC.SPINNER, model("query/czeux6pkp1l73gb/10"),
                        view(R.id.year, R.layout.item_sell_year_drop, R.layout.item_sell_year_header))
                .componentPhoto(R.id.photo, R.id.img, 0)
                .editGallery(R.id.gal, R.id.plus, R.id.del,
                        R.id.mov, 0, "query/czeux6pkp1l73gb/");

        activity(MAIN, R.layout.activity_main)
                .componentSequence(INTRO, AUTH, MAIN_1);

        activity(INTRO, R.layout.activity_intro)
                .componentIntro(model("query/czeux6pkp1l73gb/11"), R.id.intro, R.layout.item_intro_intro_0, 0, R.id.skip, R.id.cont, R.id.proc);

        activity(AUTH, R.layout.activity_auth)
                .component(TC.PAGER_F, view(R.id.pager,
                        new String[] {SIGNIN, SIGNUP})
                        .setTab(R.id.tab_layout, R.array.auth_tab_layout));

        fragment(SIGNIN, R.layout.fragment_signin)
                .component(TC.PANEL_ENTER, null,
                        view(R.id.form),
                        navigator(send(R.id.send, model(POST, "autch/czeux6pkp1l73gb/1", "login,password")
                                , after(setToken(), setProfile("profile"), handler(0, VH.NEXT_SCREEN_SEQUENCE)),
                                false, R.id.login)));

        fragment(SIGNUP, R.layout.fragment_signup)
                .component(TC.PANEL_ENTER, null,
                        view(R.id.scroll_form),
                        navigator(send(R.id.send, model(POST, "autch/czeux6pkp1l73gb/2", "login,password,name,last_name,photo,email,phone")
                                , after(setToken(), setProfile("profile"), handler(0, VH.NEXT_SCREEN_SEQUENCE)),
                                false, R.id.login, R.id.password, R.id.name, R.id.last_name, R.id.photo, R.id.email, R.id.phone)));

        fragment(PROFILE, R.layout.fragment_profile, R.string.profile_screen_title)
                .navigator(send(R.id.send, R.id.scroll_form, model(POST, "autch/czeux6pkp1l73gb/3", "name,last_name,email,phone,photo")
                        , after(setProfile("profile")),
                        false, R.id.name, R.id.last_name, R.id.email, R.id.phone))
                .component(TC.PANEL_ENTER, model(PROFILE),
                        view(R.id.scroll_form))
                .componentPhoto(R.id.ph_cl, new int[] {R.id.photo, R.id.ph}, R.string.profile_photo);

        fragment(BUY, R.layout.fragment_buy, R.string.buy_screen_title)
                .list(model("query/czeux6pkp1l73gb/19"),
                        view(R.id.list, R.layout.item_buy_list_0),
                        navigator(start(DETAIL_PROD)));

        fragment(DETAIL_PROD, R.layout.fragment_detail_prod, R.string.detail_prod_screen_title, "name_marka,name_model")
                .component(TC.PANEL, model("query/czeux6pkp1l73gb/21", "id_product"),
                        view(R.id.scroll_panel),
                        navigator(start(R.id.seller,DETAIL_USER)));

        fragment(DETAIL_USER, R.layout.fragment_detail_user, R.string.detail_user_screen_title, "name")
                .component(TC.PANEL, model("query/czeux6pkp1l73gb/22", "id_user"),
                        view(R.id.scroll_panel));

        fragment(MYADS, R.layout.fragment_myads, R.string.myads_screen_title)
                .list(model("query/czeux6pkp1l73gb/30", "id_user=!!__!!id_user"),
                        view(R.id.list, R.layout.item_myads_list_0),
                        navigator(start(R.id.T_0,DETAIL_MY),
                                delete(R.id.del, model(POST, "query/czeux6pkp1l73gb/34", "id_product")
                                        , after(handler(0, VH.DEL_ITEM_LIST)))));

        activity(DETAIL_MY, R.layout.activity_detail_my)
                .component(TC.PANEL, model("query/czeux6pkp1l73gb/31", "id_product"),
                        view(R.id.scroll_panel));

        fragment(EMALATOR, R.layout.fragment_emalator, R.string.emalator_screen_title)
                .component(TC.PANEL, model(JSON, "[{\"id_field\":1,\"aa\":\"aaaaa\",\"bb\":\"bbbbb\",\"cc\":\"ccccc\",\"dd\":\"https://deb-apps.dp-ide.com/img_app/czeux6pkp1l73gb/autosalon.jpg\"}]"),
                        view(R.id.panel))
                .list(model("query/czeux6pkp1l73gb/35"),
                        view(R.id.list, R.layout.item_emalator_list_0));

    }

    Menu menuMain_1Menu_b = new Menu()
            .item(R.drawable.news, R.string.main_1_menu_b_0, NEWS, true)
            .item(R.drawable.promotion, R.string.main_1_menu_b_1, PROMOT)
            .item(R.drawable.shoppingcard, R.string.main_1_menu_b_2, BUY);
    Menu menu_Drawer_Menu = new Menu()
            .item(R.drawable.news, R.string.drawer_menu_0, NEWS, true)
            .item(R.drawable.even, R.string.drawer_menu_1, PROMOT)
            .item(R.drawable.shoppingcard, R.string.drawer_menu_2, SELL).enabled(1)
            .item(R.drawable.icon_profile, R.string.drawer_menu_3, PROFILE).enabled(1)
            .item(R.drawable._commute_ff000000, R.string.drawer_menu_4, MYADS).enabled(1)
            .divider()        .item(0, R.string.drawer_menu_5, EMALATOR);
}
