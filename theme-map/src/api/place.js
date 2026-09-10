import { serverAxios } from "@/util/http-commons";

const server = serverAxios();
const url = "/place";

function kakaoToDto(kakao) {
  return {
    placeId: kakao.id,
    placeName: kakao.place_name,
    latitude: kakao.y,
    longitude: kakao.x,
    scoreSum: "0",
    scoreCount: "0",
    address: kakao.road_address_name || kakao.address_name,
    phone: kakao.phone,
  };
}

function dtoToKakao(dto) {
  return {
    id: dto.placeId,
    phone: dto.phone,
    place_name: dto.placeName,
    place_url: `https://place.map.kakao.com/${dto.placeId}`,
    road_address_name: dto.address,
    x: dto.longitude,
    y: dto.latitude,
    address_name: dto.address,
    category_group_code: "",
    category_group_name: "",
    category_name: "",
    distance:
      Number(dto.scoreCount) === 0
        ? 0
        : (Number(dto.scoreSum) / Number(dto.scoreCount)).toFixed(1),
  };
}

const hotPlace = (success, fail) =>
  server.get(`${url}/hot`).then(success).catch(fail);
const themePlace = (themeId, success, fail) =>
  server.get(`${url}/theme/${themeId}`).then(success).catch(fail);
const createPlace = (place, success, fail) =>
  server.post(`${url}/create`, place).then(success).catch(fail);
const linkPlace = (link, success, fail) =>
  server.post(`${url}/link`, link).then(success).catch(fail);
const keepScore = (score, success, fail) =>
  server.put(`${url}/score`, score).then(success).catch(fail);
const isThere = (placeId, success, fail) =>
  server.get(`${url}/isThere/${placeId}`).then(success).catch(fail);
const isInTheme = (themeId, placeId, success, fail) =>
  server
    .get(`${url}/isInTheme/${themeId}/${placeId}`)
    .then(success)
    .catch(fail);
const whoCreated = (themeId, placeId, success, fail) =>
  server.get(`${url}/who/${themeId}/${placeId}`).then(success).catch(fail);
const deletePlace = (themeId, placeId, success, fail) =>
  server
    .delete(`${url}/delete/${themeId}/${placeId}`)
    .then(success)
    .catch(fail);
const getSpareNum = (themeId, editorId, success, fail) =>
  server.get(`${url}/spare/${themeId}/${editorId}`).then(success).catch(fail);

export {
  hotPlace,
  themePlace,
  createPlace,
  linkPlace,
  kakaoToDto,
  dtoToKakao,
  keepScore,
  isThere,
  isInTheme,
  whoCreated,
  getSpareNum,
  deletePlace,
};
