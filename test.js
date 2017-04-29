const bigassjson = require("./out.json");
let newjson = {};

function rec(json) {
	for (const thing in bigassjson) {
		console.log(newjson);
		if (bigassjson[thing].locName) {
			newjson[thing] = bigassjson[thing];
		} else if (typeof bigassjson[thing] === "object") {
			rec(bigassjson[thing]);
		}
	}
}

rec();
console.log(newjson);
