package org.wt.example2
/**
 * run this script, print result before asm transform.
 * then run AsmTest, transform class.
 * then run this script again, print result after asm transform.
 *
 */
def res = new org.wt.example1.Demo().hello()
println res
