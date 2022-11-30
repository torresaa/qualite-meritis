import { Banque } from "./Banque";
import { Coffre } from "./Coffre";
describe("Banque tests", () => {
  test("une nouvelle banque doit être créée", () => {
    const banque = new Banque();
    expect(banque).toBeDefined();
  });
  test("3 coffres fermés doivent être ajoutés", () => {
    const n = 3;
    const banque = new Banque();
    for (let i = 0; i < n; i++) {
      const coffre = new Coffre({
        nombreDePositions: 2,
        nombreDeSymboles: 3,
      });
      banque.ajouteCoffre(coffre);
    }
    expect(banque.lesCoffresFermes.length).toBe(3);
  });

  test("3 coffres ajoutés, 3 coffres hackés, il ne reste plus de coffres fermés", () => {
    const n = 3;
    const banque = new Banque();
    for (let i = 0; i < n; i++) {
      const coffre = new Coffre({
        nombreDePositions: 2,
        nombreDeSymboles: 3,
      });
      banque.ajouteCoffre(coffre);
      coffre.ouvre();
    }
    expect(banque.lesCoffresFermes.length).toBe(0);
  });
});
