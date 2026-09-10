import { serverAxios } from "@/util/http-commons";

const server = serverAxios();

const editorConfirm = (editor, success, fail) =>
  server.post("/editor/login", editor).then(success).catch(fail);
const findById = (_id, success, fail) =>
  server.get("/editor/info").then(success).catch(fail);
const editorName = (id, success, fail) =>
  server.get(`/editor/name/${id}`).then(success).catch(fail);
const tokenRegeneration = (_editor, success, fail) =>
  server.post("/editor/refresh").then(success).catch(fail);
const regist = (editor, success, fail) =>
  server.post("/editor/regist", editor).then(success).catch(fail);
const logout = (_id, success, fail) =>
  server.post("/editor/logout").then(success).catch(fail);
const modify = (editor, success, fail) =>
  server.patch("/editor/modify", editor).then(success).catch(fail);
const resign = (_id, success, fail) =>
  server.delete("/editor/resign").then(success).catch(fail);
const power = (success, fail) =>
  server.get("/editor/power").then(success).catch(fail);

export {
  editorConfirm,
  findById,
  editorName,
  tokenRegeneration,
  regist,
  logout,
  modify,
  resign,
  power,
};
