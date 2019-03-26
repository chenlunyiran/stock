
// element为 input dom元素，digits为小数位数，默认不传为2
function formatMoney(value, digits) {
    console.info(value)
    var money = value;
    digits = digits > 0 && digits <= 20 ? digits : 2;
    money = parseFloat((money + "").replace(/[^\d\.-]/g, "")).toFixed(digits) + "";
    var l = money.split(".")[0].split("").reverse(),
        r = money.split(".")[1];
    t = "";
    for (i = 0; i < l.length; i++) {
        t += l[i] + ((i + 1) % 3 === 0 && (i + 1) !== l.length ? "," : "");
    }
    value = t.split("").reverse().join("") + "." + r;
    return value;
}