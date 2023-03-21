package logic

extension (inline sc: StringContext) {
  inline def proposition(inline args: Proposition*): Proposition =
    ${ logic.propositionCode('sc, 'args) }
}
