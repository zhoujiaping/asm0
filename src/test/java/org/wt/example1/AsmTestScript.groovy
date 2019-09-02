package org.wt.example1
/**
 * run this script, print result before asm transform.
 * then run AsmTest, transform class.
 * then run this script again, print result after asm transform.
 *
 */
def res = new Demo().hello()
println res
