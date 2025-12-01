@file:JvmName("NamedColors")
/**
 * Css colors
 */
package dev.mayaqq.cynosure.utils.colors

@JvmSynthetic
@JvmField
internal val colorByName: MutableMap<String, Color> = LinkedHashMap(148)

@JvmSynthetic
@JvmField
internal val nameByColor: MutableMap<Color, String> = HashMap(148)

private fun namedColor(name: String, color: UInt): Color = Color(color or 0xff000000u).also {
    colorByName[name] = it
    if (!nameByColor.contains(it)) nameByColor[it] = name
}

private fun namedColorWithAlpha(name: String, color: UInt): Color = Color(color).also {
    colorByName[name] = it
    if (!nameByColor.contains(it)) nameByColor[it] = name
}

// CSS Colors
public val AliceBlue: Color = namedColor("aliceblue", 0xf0f8ffu)

public val AntiqueWhite: Color = namedColor("antiquewhite", 0xfaebd7u)

public val Aqua: Color = namedColor("aqua", 0x00ffffu)

public val Aquamarine: Color = namedColor("aquamarine", 0x7fffd4u)

// omg azure reference
public val Azure: Color = namedColor("azure", 0xf0ffffu)

public val Beige: Color = namedColor("beige", 0xf5f5dcu)

public val Bisque: Color = namedColor("bisque", 0xffe4c4u)

public val Black: Color = namedColor("black", 0x000000u)

public val BlanchedAlmond: Color = namedColor("blanchedalmond", 0xffebcdu)

public val Blue: Color = namedColor("blue", 0x0000ffu)

public val BlueViolet: Color = namedColor("blueviolet", 0x8a2be2u)

public val BohemianBlue: Color = namedColor("bohemianblue", 0x0000aau)

public val Brown: Color = namedColor("brown", 0xa52a2au)

public val Burlywood: Color = namedColor("burlywood", 0xdeb887u)

public val CadetBlue: Color = namedColor("cadetblue", 0x5f9ea0u)

public val Chartreuse: Color = namedColor("chartreuse", 0x7fff00u)

public val Chocolate: Color = namedColor("chocolate", 0xd2691eu)

public val Coral: Color = namedColor("coral", 0xff7f50u)

public val CornflowerBlue: Color = namedColor("cornflowerblue", 0x6495edu)

public val Cornsilk: Color = namedColor("cornsilk", 0xfff8dcu)

public val Crimson: Color = namedColor("crimson", 0xdc143cu)

public val Cyan: Color = namedColor("cyan", 0x00ffffu)

public val DarkBlue: Color = namedColor("darkblue", 0x00008bu)

public val DarkCyan: Color = namedColor("darkcyan", 0x008b8bu)

public val DarkGoldenRod: Color = namedColor("darkgoldenrod", 0xb8860bu)

public val DarkGray: Color = namedColor("darkgray", 0xa9a9a9u)

public val DarkGreen: Color = namedColor("darkgreen", 0x006400u)

public val DarkGrey: Color = namedColor("darkgrey", 0xa9a9a9u)

public val DarkKhaki: Color = namedColor("darkkhaki", 0xbdb76bu)

public val DarkMagenta: Color = namedColor("darkmagenta", 0x8b008bu)

public val DarkOliveGreen: Color = namedColor("darkolivegreen", 0x556b2fu)

public val DarkOrange: Color = namedColor("darkorange", 0xff8c00u)

public val DarkOrchid: Color = namedColor("darkorchid", 0x9932ccu)

public val DarkRed: Color = namedColor("darkred", 0x8b0000u)

public val DarkSalmon: Color = namedColor("darksalmon", 0xe9967au)

public val DarkSeaGreen: Color = namedColor("darkseagreen", 0x8fbc8fu)

public val DarkSlateBlue: Color = namedColor("darkslateblue", 0x483d8bu)

public val DarkSlateGray: Color = namedColor("darkslategray", 0x2f4f4fu)

public val DarkSlateGrey: Color = namedColor("darkslategrey", 0x2f4f4fu)

public val DarkTurquoise: Color = namedColor("darkturquoise", 0x00ced1u)

public val DarkViolet: Color = namedColor("darkviolet", 0x9400d3u)

public val DavysGrey: Color = namedColor("davysgrey", 0x555555u)

public val DeepPink: Color = namedColor("deeppink", 0xff1493u)

public val DeepSkyblue: Color = namedColor("deepskyblue", 0x00bfffu)

public val DhusarGrey: Color = namedColor("dhusargrey", 0xaaaaaau)

public val DimGray: Color = namedColor("dimgray", 0x696969u)

public val DimGrey: Color = namedColor("dimgrey", 0x696969u)

public val DodgerBlue: Color = namedColor("dodgerblue", 0x1e90ffu)

public val Firebrick: Color = namedColor("firebrick", 0xb22222u)

public val FlashOfOrange: Color = namedColor("flashoforange", 0xffaa00u)

public val FloralWhite: Color = namedColor("floralwhite", 0xfffaf0u)

public val FluorescentRed: Color = namedColor("fluorescentred", 0xFF5555u)

public val ForestGreen: Color = namedColor("forestgreen", 0x228b22u)

public val Fuchsia: Color = namedColor("fuchsia", 0xff00ffu)

public val Gainsboro: Color = namedColor("gainsboro", 0xdcdcdcu)

public val GhostWhite: Color = namedColor("ghostwhite", 0xf8f8ffu)

public val Goldenrod: Color = namedColor("goldenrod", 0xdaa520u)

public val Gold: Color = namedColor("gold", 0xffd700u)

public val Gray: Color = namedColor("gray", 0x808080u)

public val Green: Color = namedColor("green", 0x008000u)

public val GreenYellow: Color = namedColor("greenyellow", 0xadff2fu)

public val Grey: Color = namedColor("grey", 0x808080u)

public val Honeydew: Color = namedColor("honeydew", 0xf0fff0u)

public val HotPink: Color = namedColor("hotpink", 0xff69b4u)

public val IndianRed: Color = namedColor("indianred", 0xcd5c5cu)

public val Indigo: Color = namedColor("indigo", 0x4b0082u)

public val IslamicGreen: Color = namedColor("islamicgreen", 0x00AA00u)

public val Ivory: Color = namedColor("ivory", 0xfffff0u)

public val Khaki: Color = namedColor("khaki", 0xf0e68cu)

public val LavenderBlush: Color = namedColor("lavenderblush", 0xfff0f5u)

public val Lavender: Color = namedColor("lavender", 0xe6e6fau)

public val LawnGreen: Color = namedColor("lawngreen", 0x7cfc00u)

public val LemonChiffon: Color = namedColor("lemonchiffon", 0xfffacdu)

public val LightBlue: Color = namedColor("lightblue", 0xadd8e6u)

public val LightCoral: Color = namedColor("lightcoral", 0xf08080u)

public val LightCyan: Color = namedColor("lightcyan", 0x55ffffu)

public val LightCyan1: Color = namedColor("lightcyan", 0xe0ffffu)

public val LightGoldenrodYellow: Color = namedColor("lightgoldenrodyellow", 0xfafad2u)

public val LightGray: Color = namedColor("lightgray", 0xd3d3d3u)

public val LightGreen: Color = namedColor("lightgreen", 0x90ee90u)

public val LightGrey: Color = namedColor("lightgrey", 0xd3d3d3u)

public val LightLimeGreen: Color = namedColor("lightlimegreen", 0x55ff55u)

public val LightMagenta: Color = namedColor("lightmagenta", 0xff55ffu)

public val LightPink: Color = namedColor("lightpink", 0xffb6c1u)

public val LightSalmon: Color = namedColor("lightsalmon", 0xffa07au)

public val LightSeaGreen: Color = namedColor("lightseagreen", 0x20b2aau)

public val LightSkyBlue: Color = namedColor("lightskyblue", 0x87cefau)

public val LightSlateGray: Color = namedColor("lightslategray", 0x778899u)

public val LightSlateGrey: Color = namedColor("lightslategrey", 0x778899u)

public val LightSteelBlue: Color = namedColor("lightsteelblue", 0xb0c4deu)

public val LightYellow: Color = namedColor("lightyellow", 0xffffe0u)

public val Lime: Color = namedColor("lime", 0x00ff00u)

public val LimeGreen: Color = namedColor("limegreen", 0x32cd32u)

public val Linen: Color = namedColor("linen", 0xfaf0e6u)

public val Magenta: Color = namedColor("magenta", 0xff00ffu)

public val Maroon: Color = namedColor("maroon", 0x800000u)

public val MediumAquamarine: Color = namedColor("mediumaquamarine", 0x66cdaau)

public val MediumBlue: Color = namedColor("mediumblue", 0x0000cdu)

public val MediumOrchid: Color = namedColor("mediumorchid", 0xba55d3u)

public val MediumPurple: Color = namedColor("mediumpurple", 0x9370dbu)

public val MediumSeaGreen: Color = namedColor("mediumseagreen", 0x3cb371u)

public val MediumSlateBlue: Color = namedColor("mediumslateblue", 0x7b68eeu)

public val MediumSpringGreen: Color = namedColor("mediumspringgreen", 0x00fa9au)

public val MediumTurquoise: Color = namedColor("mediumturquoise", 0x48d1ccu)

public val MediumVioletRed: Color = namedColor("mediumvioletred", 0xc71585u)

public val MidnightBlue: Color = namedColor("midnightblue", 0x191970u)

public val MintCream: Color = namedColor("mintcream", 0xf5fffau)

public val MistyRose: Color = namedColor("mistyrose", 0xffe4e1u)

public val Moccasin: Color = namedColor("moccasin", 0xffe4b5u)

public val NavajoWhite: Color = namedColor("navajowhite", 0xffdeadu)

public val Navy: Color = namedColor("navy", 0x000080u)

public val OldLace: Color = namedColor("oldlace", 0xfdf5e6u)

public val Olive: Color = namedColor("olive", 0x808000u)

public val OliveDrab: Color = namedColor("olivedrab", 0x6b8e23u)

public val Orange: Color = namedColor("orange", 0xffa500u)

public val OrangeRed: Color = namedColor("orangered", 0xff4500u)

public val Orchid: Color = namedColor("orchid", 0xda70d6u)

public val PaleGoldenrod: Color = namedColor("palegoldenrod", 0xeee8aau)

public val PaleGreen: Color = namedColor("palegreen", 0x98fb98u)

public val PaleTurquoise: Color = namedColor("paleturquoise", 0xafeeeeu)

public val PaleVioletRed: Color = namedColor("palevioletred", 0xdb7093u)

public val PapayaWhip: Color = namedColor("papayawhip", 0xffefd5u)

public val PeachPuff: Color = namedColor("peachpuff", 0xffdab9u)

public val Peru: Color = namedColor("peru", 0xcd853fu)

public val Pink: Color = namedColor("pink", 0xffc0cbu)

public val Plum: Color = namedColor("plum", 0xdda0ddu)

public val PowderBlue: Color = namedColor("powderblue", 0xb0e0e6u)

public val Purple: Color = namedColor("purple", 0x800080u)

public val RebeccaPurple: Color = namedColor("rebeccapurple", 0x663399u)

public val Red: Color = namedColor("red", 0xff0000u)

public val RosyBrown: Color = namedColor("rosybrown", 0xbc8f8fu)

public val RoyalBlue: Color = namedColor("royalblue", 0x4169e1u)

public val SaddleBrown: Color = namedColor("saddlebrown", 0x8b4513u)

public val Salmon: Color = namedColor("salmon", 0xfa8072u)

public val SandyBrown: Color = namedColor("sandybrown", 0xf4a460u)

public val SeaGreen: Color = namedColor("seagreen", 0x2e8b57u)

public val Seashell: Color = namedColor("seashell", 0xfff5eeu)

public val Sienna: Color = namedColor("sienna", 0xa0522du)

public val Silver: Color = namedColor("silver", 0xc0c0c0u)

public val SkyBlue: Color = namedColor("skyblue", 0x87ceebu)

public val SlateBlue: Color = namedColor("slateblue", 0x6a5acdu)

public val SlateGray: Color = namedColor("slategray", 0x708090u)

public val SlateGrey: Color = namedColor("slategrey", 0x708090u)

public val Snow: Color = namedColor("snow", 0xfffafau)

public val SpringGreen: Color = namedColor("springgreen", 0x00ff7fu)

public val SteelBlue: Color = namedColor("steelblue", 0x4682b4u)

public val Tan: Color = namedColor("tan", 0xd2b48cu)

public val Teal: Color = namedColor("teal", 0x008080u)

public val Thistle: Color = namedColor("thistle", 0xd8bfd8u)

public val TiffanyBlue: Color = namedColor("tiffanyblue", 0x00aaaau)

public val Tomato: Color = namedColor("tomato", 0xff6347u)

public val Turquoise: Color = namedColor("turquoise", 0x40e0d0u)

public val UnnamedLightBlue: Color = namedColor("unnamedlightblue", 0x5555ffu)
public val UnnamedLightYellow: Color = namedColor("unnamedlightyellow", 0xffff55u)
public val UnnamedMagenta: Color = namedColor("unnamedmagenta", 0xaa00aau)

public val Violet: Color = namedColor("violet", 0xee82eeu)

public val Wheat: Color = namedColor("wheat", 0xf5deb3u)

public val White: Color = namedColor("white", 0xffffffu)

public val WhiteSmoke: Color = namedColor("whitesmoke", 0xf5f5f5u)

public val Yellow: Color = namedColor("yellow", 0xffff00u)

public val YellowGreen: Color = namedColor("yellowgreen", 0x9acd32u)

public val Transparent: Color = namedColorWithAlpha("transparent", 0x00000000u)

// Minecraft Colors
public val McBlack: Color get() = Black
public val McDarkBlue: Color get() = BohemianBlue
public val McDarkGreen: Color get() = IslamicGreen
public val McDarkAqua: Color get() = TiffanyBlue
public val McDarkRed: Color get() = Red
public val McDarkPurple: Color get() = UnnamedMagenta
public val McMagenta: Color get() = UnnamedMagenta
public val McGold: Color get() = FlashOfOrange
public val McOrange: Color get() = FlashOfOrange
public val McGray: Color get() = DhusarGrey
public val McDarkGray: Color get() = DavysGrey
public val McBlue: Color get() = UnnamedLightBlue
public val McGreen: Color get() = LightLimeGreen
public val McAqua: Color get() = LightCyan
public val McRed: Color get() = FluorescentRed
public val McLightPurple: Color get() = LightMagenta
public val McPink: Color get() = LightMagenta
public val McYellow: Color get() = UnnamedLightYellow
public val McWhite: Color get() = White
