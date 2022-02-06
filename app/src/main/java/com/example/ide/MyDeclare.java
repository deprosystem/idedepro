package com.example.ide;
import com.dpcsa.compon.base.DeclareScreens;
import com.dpcsa.compon.interfaces_classes.Menu;
import com.dpcsa.compon.interfaces_classes.Multiply;
import com.dpcsa.compon.interfaces_classes.ToolMenu;
import com.dpcsa.compon.interfaces_classes.ViewHandler;

import static com.dpcsa.compon.interfaces_classes.ToolMenu.STATUS_START.VIEW;
import static com.dpcsa.compon.interfaces_classes.ToolMenu.STATUS_VIEW.ALWAYS;

public class MyDeclare extends DeclareScreens {
    public final static String
            MAIN = "MAIN", NEW = "NEW", START = "START",
            DETAIL = "DETAIL", INTRO = "INTRO", AUTH = "AUTH",
            SHARE = "SHARE", SIN = "SIN", SUP = "SUP",
            DRAWER = "DRAWER", PROF = "PROF", SALL = "SALL";

    @Override
    public void declare() {

        activity(MAIN, R.layout.activity_main)
                .toolBar(R.id.tool_bar)
                .menuBottom(model(menuMainMenu_b), view(R.id.menu_b))
                .drawer(R.id.drawer, R.id.container_fragm, R.id.left_drawer, null, DRAWER);

        fragment(NEW, R.layout.fragment_new, R.string.new_screen_title)
                .list(model("query/nk3mek0vtopbdoh/4"),
                        view(R.id.list, R.layout.item_new_list_0),
                        navigator(start(DETAIL)));

        activity(START, R.layout.activity_start, R.string.start_screen_title)
                .componentSequence(INTRO, AUTH, MAIN);

        fragment(DETAIL, R.layout.fragment_detail, R.string.detail_screen_title, "title").animate(AS.RL)
                .component(TC.PANEL, model("query/nk3mek0vtopbdoh/7", "id_new"),
                        view(R.id.scroll_panel));

        activity(INTRO, R.layout.activity_intro)
                .componentIntro(model("query/nk3mek0vtopbdoh/11"), R.id.intro, R.layout.item_intro_intro_0 , R.id.indi, R.id.skip, R.id.contin, R.id.proceed);

        activity(AUTH, R.layout.activity_auth, R.string.auth_screen_title)
                .component(TC.PAGER_F, view(R.id.pager,
                        new String[] {SIN, SUP})
                        .setTab(R.id.tab_layout, R.array.auth_tab_layout))
                .toolBar(R.id.tool_bar);

        fragment(SHARE, R.layout.fragment_share, R.string.share_screen_title);
        fragment(SIN, R.layout.fragment_sin)
                .component(TC.PANEL_ENTER, null,
                        view(R.id.form),
                        navigator(send(R.id.send, model(POST, "autch/nk3mek0vtopbdoh/1", "login,password")
                                , after(setToken(), setProfile("profile"), handler(0, VH.NEXT_SCREEN_SEQUENCE)),
                                false, R.id.login, R.id.password),
                                handler(R.id.noauth, VH.NEXT_SCREEN_SEQUENCE)));

        fragment(SUP, R.layout.fragment_sup)
                .navigator(send(R.id.send, R.id.scroll_form, model(POST, "autch/nk3mek0vtopbdoh/2", "login,password,f_name,l_name,email,phone,photo")
                        , after(setToken(), setProfile("profile"), handler(0, VH.NEXT_SCREEN_SEQUENCE)),
                        false, R.id.login, R.id.password, R.id.f_name, R.id.l_name, R.id.email, R.id.phone))
                .component(TC.PANEL_ENTER, null,
                        view(R.id.scroll_form))
                .componentPhoto(R.id.cam, R.id.photo, R.string.sup_photo);

        fragment(DRAWER, R.layout.fragment_drawer)
                .navigator(exit(R.id.exit),
                        start(R.id.enter,AUTH))
                .component(TC.PANEL, model(PROFILE),
                        view(R.id.panel).noDataView(R.id.nodata))
                .menu(model(menu_Drawer_Menu), view(R.id.menu));

        fragment(PROF, R.layout.fragment_prof, R.string.prof_screen_title)
                .component(TC.PANEL_ENTER, model(PROFILE),
                        view(R.id.scroll_form),
                        navigator(send(R.id.send, model(POST, "autch/nk3mek0vtopbdoh/3", "f_name,l_name,email,phone,photo")
                                , after(setProfile("profile")),
                                false, R.id.f_name, R.id.l_name, R.id.email, R.id.phone)))
                .componentPhoto(R.id.click_ph, new int[] {R.id.photo, R.id.ph_oval}, R.string.prof_photo);

        fragment(SALL, R.layout.fragment_sall, R.string.sall_screen_title)
                .editGallery(R.id.gal, R.id.add, R.id.del,
                        R.id.move, R.string.sall_gal, "query/nk3mek0vtopbdoh/")
                .component(TC.SPINNER, model("query/nk3mek0vtopbdoh/17"),
                        view(R.id.spinner, R.layout.item_sall_spinner_drop, R.layout.item_sall_spinner_header)
                        ,navigator(back().setTypeEvent(ViewHandler.evCHANGE)));

    }

    Menu menuMainMenu_b = new Menu()
            .item(R.drawable.news, R.string.main_menu_b_0, NEW, true)
            .item(R.drawable.promotion, R.string.main_menu_b_1, SHARE);
    Menu menu_Drawer_Menu = new Menu()
            .item(R.drawable.news, R.string.drawer_menu_0, NEW, true)
            .item(R.drawable.promotion, R.string.drawer_menu_1, SHARE)
            .item(R.drawable.icon_profile, R.string.drawer_menu_2, PROF).enabled(1)
            .item(R.drawable._verified_ff000000, R.string.drawer_menu_3, SALL);
}
