package testing

opaque type Day = Int

object Day {
  // We can use macros to create custom refined types...
  inline def applyMacro(day: Int): Day = ${ dayMacroCode('day) }

  // ...but we can have the exact same validation logic via an inline method (bonus: this can be defined in the same file)
  inline def applyInline(day: Int): Day =
    if (day < 1 || day > 31)
      scala.compiletime.error("Day must be between 1 and 31!")
    else
      day
}
