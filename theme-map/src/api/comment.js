import { serverAxios } from "@/util/http-commons";

const server = serverAxios();
const url = "/comment";

const registComment = (comment, success, fail) =>
  server.post(`${url}/regist`, comment).then(success).catch(fail);
const commentsOfPlace = (placeId, success, fail) =>
  server.get(`${url}/comments/${placeId}`).then(success).catch(fail);

export { registComment, commentsOfPlace };
