import { describe, expect, it } from "vitest";

import { kakaoToDto } from "./place";

describe("kakaoToDto", () => {
  it("creates an independent DTO for every search result", () => {
    const first = kakaoToDto({
      id: "1",
      place_name: "첫 장소",
      x: "127",
      y: "37",
      address_name: "서울",
      phone: "",
    });
    const second = kakaoToDto({
      id: "2",
      place_name: "둘째 장소",
      x: "128",
      y: "36",
      address_name: "부산",
      phone: "",
    });

    expect(first).not.toBe(second);
    expect(first.placeId).toBe("1");
    expect(second.placeId).toBe("2");
  });

  it("prefers the road address when Kakao provides one", () => {
    const place = kakaoToDto({
      id: "1",
      place_name: "장소",
      x: "127",
      y: "37",
      road_address_name: "서울시 도로명 1",
      address_name: "서울시 지번 2",
      phone: "",
    });

    expect(place.address).toBe("서울시 도로명 1");
  });
});
