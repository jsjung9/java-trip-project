import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "home",
      component: () => import("@/views/TheMapView.vue"),
      redirect: "/place",
      children: [
        {
          path: "place",
          name: "place",
          component: () => import("@/components/map/PlaceList.vue"),
        },
      ],
    },
    {
      path: "/theme",
      name: "theme",
      component: () => import("@/views/TheThemeView.vue"),
      redirect: "/theme/main",
      children: [
        {
          path: "main",
          name: "main",
          component: () => import("@/views/TheThemeMain.vue"),
        },
        {
          path: "detail/:themeId",
          name: "detail",
          component: () => import("@/views/TheMapView.vue"),
        },
        {
          path: "create",
          name: "create",
          component: () => import("@/components/theme/ThemeCreate.vue"),
          meta: { requiresAuth: true },
        },
        {
          path: "keyword/:themeId",
          name: "keyword",
          component: () => import("@/components/map/KeywordPlace.vue"),
          meta: { requiresAuth: true },
        },
        {
          path: "modify/:themeId",
          name: "themeModify",
          component: () => import("@/components/theme/ThemeModify.vue"),
          meta: { requiresAuth: true },
        },
      ],
    },
    {
      path: "/editor",
      name: "editor",
      component: () => import("@/views/TheEditorMain.vue"),
    },
    {
      path: "/login",
      name: "login",
      component: () => import("@/components/editor/EditorLogin.vue"),
    },
    {
      path: "/regist",
      name: "regist",
      component: () => import("@/components/editor/EditorRegist.vue"),
    },
    {
      path: "/mypage",
      name: "mypage",
      component: () => import("@/components/editor/EditorMyPage.vue"),
      meta: { requiresAuth: true },
    },
    {
      path: "/modify",
      name: "modify",
      component: () => import("@/components/editor/EditorModify.vue"),
      meta: { requiresAuth: true },
    },
  ],
});

router.beforeEach((to) => {
  if (to.meta.requiresAuth && !sessionStorage.getItem("accessToken")) {
    return { name: "login", query: { redirect: to.fullPath } };
  }
  return true;
});

export default router;
