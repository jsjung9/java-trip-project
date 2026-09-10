import { computed, ref } from "vue";
import { defineStore } from "pinia";
import { useRouter } from "vue-router";

import {
  editorConfirm,
  findById,
  logout,
  modify,
  resign,
  tokenRegeneration,
} from "@/api/editor";

const emptyEditor = () => ({
  editorId: "",
  id: "",
  emailId: "",
  emailDomain: "",
  editorName: "",
  likeSum: "0",
  joinDate: "",
});

export const useEditorStore = defineStore(
  "editorStore",
  () => {
    const router = useRouter();
    const isLogin = ref(Boolean(sessionStorage.getItem("accessToken")));
    const isLoginError = ref(false);
    const editorInfo = ref(emptyEditor());
    const editorDto = ref(emptyEditor());
    const sEditorDto = ref({ ...emptyEditor(), token: false });
    const isValidToken = ref(isLogin.value);

    const cEditorInfo = computed(() => editorInfo.value);
    const cEditorDto = computed(() => editorDto.value);
    const cCurEditorDto = computed(() => sEditorDto.value);
    const cIsLogin = computed(() => isLogin.value);

    const clearSession = () => {
      sessionStorage.removeItem("accessToken");
      isLogin.value = false;
      isValidToken.value = false;
      editorInfo.value = emptyEditor();
      editorDto.value = emptyEditor();
      sEditorDto.value = { ...emptyEditor(), token: false };
    };

    const editorLogin = (credentials) =>
      editorConfirm(
        credentials,
        (response) => {
          const accessToken = response.data["access-token"];
          sessionStorage.setItem("accessToken", accessToken);
          editorDto.value = response.data.editor;
          editorInfo.value = response.data.editor;
          sEditorDto.value = { ...response.data.editor, token: true };
          isLogin.value = true;
          isValidToken.value = true;
          isLoginError.value = false;
        },
        () => {
          clearSession();
          isLoginError.value = true;
        },
      );

    const getEditorInfo = () =>
      findById(
        null,
        (response) => {
          editorInfo.value = response.data.editorInfo;
          editorDto.value = response.data.editorInfo;
          isLogin.value = true;
          isValidToken.value = true;
        },
        () => tokenRegenerate(),
      );

    const tokenRegenerate = () =>
      tokenRegeneration(
        null,
        (response) => {
          sessionStorage.setItem("accessToken", response.data["access-token"]);
          isLogin.value = true;
          isValidToken.value = true;
        },
        () => {
          clearSession();
          router.push({ name: "login" });
        },
      );

    const editorLogout = (id) =>
      logout(
        id,
        () => clearSession(),
        () => clearSession(),
      );

    const editorModify = (editor) =>
      modify(editor, (response) => {
        if (response.status === 200) {
          editorDto.value = { ...editorDto.value, ...editor, pw: undefined };
          editorInfo.value = editorDto.value;
        }
      });

    const editorResign = (id) =>
      resign(
        id,
        () => clearSession(),
        () => clearSession(),
      );

    window.addEventListener("auth-expired", clearSession);

    return {
      isLogin,
      isLoginError,
      editorInfo,
      editorDto,
      sEditorDto,
      isValidToken,
      cEditorInfo,
      cEditorDto,
      cCurEditorDto,
      cIsLogin,
      editorLogin,
      getEditorInfo,
      tokenRegenerate,
      editorLogout,
      editorModify,
      editorResign,
    };
  },
  { persist: { storage: sessionStorage } },
);
