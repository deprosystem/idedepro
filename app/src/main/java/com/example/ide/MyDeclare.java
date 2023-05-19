package com.example.ide;

import com.dpcsa.compon.base.DeclareScreens;
import com.dpcsa.compon.interfaces_classes.Menu;
import com.dpcsa.compon.interfaces_classes.ViewHandler;

public class MyDeclare extends DeclareScreens {
    public final static String
            MAIN_1 = "MAIN_1", NEWS = "NEWS", PROMOT = "PROMOT",
            DETAIL_NEWS = "DETAIL_NEWS", DRAWER = "DRAWER", SELL = "SELL",
            M_ADS = "M_ADS", PROF = "PROF", MAIN = "MAIN",
            INTRO = "INTRO", AUTH = "AUTH", SIGNIN = "SIGNIN",
            SIGNUP = "SIGNUP";

    @Override
    public void declare() {

        activity(MAIN_1, R.layout.activity_main_1)
                .component(TC.TOOL, null, view(R.id.tool_bar, new int[] {R.drawable.icon_menu, R.drawable.menu, 0}))
                .menuBottom(model(menuMain_1Menu_b), view(R.id.menu_b))
                .drawer(R.id.drawer, R.id.container_fragm, R.id.left_drawer, null, DRAWER);

        fragment(NEWS, R.layout.fragment_news, R.string.news_screen_title)
                .list(model("query/w3auhtqt_bemqtc/4"),
                        view(R.id.list, R.layout.item_news_list_0),
                        navigator(start(DETAIL_NEWS)));

        fragment(PROMOT, R.layout.fragment_promot, R.string.promot_screen_title)
                .list(model("query/w3auhtqt_bemqtc/5"),
                        view(R.id.list, R.layout.item_promot_list_0));

        fragment(DETAIL_NEWS, R.layout.fragment_detail_news)
                .component(TC.PANEL, model("query/w3auhtqt_bemqtc/6"),
                        view(R.id.scroll_panel));

        fragment(DRAWER, R.layout.fragment_drawer)
                .component(TC.PANEL, model("query/w3auhtqt_bemqtc/7"),
                        view(R.id.panel).noDataView(R.id.no_v))
                .menu(model(menu_Drawer_Menu), view(R.id.menu));

        fragment(SELL, R.layout.fragment_sell, R.string.sell_screen_title);
        fragment(M_ADS, R.layout.fragment_m_ads, R.string.m_ads_screen_title);
        fragment(PROF, R.layout.fragment_prof, R.string.prof_screen_title)
                .component(TC.PANEL_ENTER, model(PROFILE),
                        view(R.id.scroll_form))
                .componentPhoto(R.id.phot_1, new int[] {R.id.photo, R.id.phot_1}, R.string.prof_photo);

        activity(MAIN, R.layout.activity_main)
                .componentSequence(INTRO, AUTH, MAIN_1);

        activity(INTRO, R.layout.activity_intro)
                .componentIntro(model("query/w3auhtqt_bemqtc/8"), R.id.intro, R.layout.item_intro_intro_0, 0, R.id.skip, R.id.cont, R.id.proc);

        activity(AUTH, R.layout.activity_auth)
                .component(TC.PAGER_F, view(R.id.pager,
                        new String[] {SIGNIN, SIGNUP})
                        .setTab(R.id.tab_layout, R.array.auth_tab_layout));

        fragment(SIGNIN, R.layout.fragment_signin)
                .component(TC.PANEL_ENTER, null,
                        view(R.id.form),
                        navigator(send(R.id.send, model(POST, "autch/w3auhtqt_bemqtc/1", "login,password")
                                , after(setToken(), setProfile("profile"), handler(0, VH.NEXT_SCREEN_SEQUENCE)),
                                false, R.id.login, R.id.password)));

        fragment(SIGNUP, R.layout.fragment_signup)
                .component(TC.PANEL_ENTER, null,
                        view(R.id.scroll_form),
                        navigator(send(R.id.send, model(POST, "autch/w3auhtqt_bemqtc/2", "login,password,name,last_name,photo,email,phone")
                                , after(setToken(), setProfile("profile"), handler(0, VH.NEXT_SCREEN_SEQUENCE)),
                                false, R.id.login, R.id.password, R.id.name, R.id.last_name, R.id.email, R.id.phone)))
                .componentPhoto(R.id.phot_btn, R.id.photo, R.string.signup_photo);

    }

    Menu menuMain_1Menu_b = new Menu()
            .item(R.drawable.news, R.string.main_1_menu_b_0, NEWS, true)
            .item(R.drawable.promotion, R.string.main_1_menu_b_1, PROMOT);
    Menu menu_Drawer_Menu = new Menu()
            .item(R.drawable.news, R.string.drawer_menu_0, NEWS, true)
            .item(R.drawable.promotion, R.string.drawer_menu_1, PROMOT)
            .item(R.drawable.shoppingcard, R.string.drawer_menu_2, SELL).enabled(1)
            .item(R.drawable._single_bed_ff000000, R.string.drawer_menu_3, M_ADS).enabled(1)
            .item(R.drawable.icon_profile, R.string.drawer_menu_4, PROF).enabled(1);
}
