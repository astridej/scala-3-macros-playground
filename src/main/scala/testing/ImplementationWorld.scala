package testing

inline def inspect(inline x: Any) : Any = ${ inspectCode('x)}
inline def inspectTyped[T](inline x: T) : T = ${ inspectCodeTyped('x)}
inline def inspectRuntime[T](inline x: T) : (String, String) = ${ inspectCodeRuntime('x)}

inline def plusStatic(inline x: Int, y: Int) : Int = ${ plusStaticCode('x, 'y)}
inline def plusDynamic(inline x: Int, y: Int) : Int = ${ plusDynamicCode('x, 'y)}

inline def compilerFibonacci(inline n: Int) : Int = ${ compilerFibonacciCode('n)}