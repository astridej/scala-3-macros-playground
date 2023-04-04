package testing

opaque type Day = Int

object Day {
  inline def applyMacro(day: Int): Day = ${ dayMacroCode('day) }

  inline def applyInline(day: Int): Day =
    if (day < 1 || day > 31)
      scala.compiletime.error("Day must be between 1 and 31!")
    else
      day
}
