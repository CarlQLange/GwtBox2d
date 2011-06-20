/*******************************************************************************
 * Copyright (c) 2011, Daniel Murphy All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of the <organization> nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL DANIEL MURPHY BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
/*
 * JBox2D - A Java Port of Erin Catto's Box2D
 * 
 * JBox2D homepage: http://jbox2d.sourceforge.net/ Box2D homepage:
 * http://www.box2d.org
 * 
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from the
 * use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following reions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not claim
 * that you wrote the original software. If you use this software in a product,
 * an acknowledgment in the product documentation would be appreciated but is
 * not required. 2. Altered source versions must be plainly marked as such, and
 * must not be misrepresented as being the original software. 3. This notice may
 * not be removed or altered from any source distribution.
 */

package org.jbox2d.common;

import java.util.Random;

/**
 * A few math methods that don't fit very well anywhere else.
 */
public class MathUtils {
    public static final float PI = (float) Math.PI;
    public static final float TWOPI = (float) (Math.PI * 2);
    public static final float INV_PI = 1f / PI;
    public static final float HALF_PI = PI / 2;
    public static final float QUARTER_PI = PI / 4;
    public static final float THREE_HALVES_PI = TWOPI - HALF_PI;

    /**
     * Degrees to radians conversion factor
     */
    public static final float DEG2RAD = PI / 180;

    /**
     * Radians to degrees conversion factor
     */
    public static final float RAD2DEG = 180 / PI;

    private static final float SHIFT23 = 1 << 23;
    private static final float INV_SHIFT23 = 1.0f / SHIFT23;

    public static final float[] sinLUT = new float[Settings.SINCOS_LUT_LENGTH];
    public static final float[] cosLUT = new float[Settings.SINCOS_LUT_LENGTH];

    static {
        for (int i = 0; i < Settings.SINCOS_LUT_LENGTH; i++) {
            sinLUT[i] = (float) Math.sin(i * Settings.SINCOS_LUT_PRECISION);
            cosLUT[i] = (float) Math.cos(i * Settings.SINCOS_LUT_PRECISION);
        }
    }

    public static final float sin(float x) {
        if (Settings.SINCOS_LUT_ENABLED) {
            return sinLUT(x);
        } else {
            return (float) Math.sin(x);
        }
    }

    public static final float sinLUT(float x) {
        x %= TWOPI;

        while (x < 0) {
            x += TWOPI;
        }

        if (Settings.SINCOS_LUT_LERP) {

            x /= Settings.SINCOS_LUT_PRECISION;

            final int index = (int) x;

            if (index != 0) {
                x %= index;
            }

            // the next index is 0
            if (index == Settings.SINCOS_LUT_LENGTH - 1) {
                return ((1 - x) * sinLUT[index] + x * sinLUT[0]);
            } else {
                return ((1 - x) * sinLUT[index] + x * sinLUT[index + 1]);
            }

        } else {
            return sinLUT[MathUtils.round(x / Settings.SINCOS_LUT_PRECISION)
                    % Settings.SINCOS_LUT_LENGTH];
        }
    }

    public static final float cos(float x) {
        if (Settings.SINCOS_LUT_ENABLED) {
            x %= TWOPI;

            while (x < 0) {
                x += TWOPI;
            }

            if (Settings.SINCOS_LUT_LERP) {

                x /= Settings.SINCOS_LUT_PRECISION;

                final int index = (int) x;

                if (index != 0) {
                    x %= index;
                }

                // the next index is 0
                if (index == Settings.SINCOS_LUT_LENGTH - 1) {
                    return ((1 - x) * cosLUT[index] + x * cosLUT[0]);
                } else {
                    return ((1 - x) * cosLUT[index] + x * cosLUT[index + 1]);
                }

            } else {
                return cosLUT[MathUtils
                        .round(x / Settings.SINCOS_LUT_PRECISION)
                        % Settings.SINCOS_LUT_LENGTH];
            }

        } else {
            return (float) Math.cos(x);
        }
    }

    public static final float abs(final float x) {
        if (Settings.FAST_MATH) {
            return x > 0 ? x : -x;
        } else {
            return Math.abs(x);
        }
    }

    public static final int abs(int x) {
        int y = x >> 31;
        return (x ^ y) - y;
    }

    public static final int floor(final float x) {
        if (Settings.FAST_MATH) {
            int y = (int) x;
            if (x < 0 && x != y) {
                y--;
            }
            return y;
        } else {
            return (int) Math.floor(x);
        }
    }

    public static final int ceil(final float x) {
        if (Settings.FAST_MATH) {
            int y = (int) x;
            if (x > 0 && x != y) {
                y++;
            }
            return y;
        } else {
            return (int) Math.ceil(x);
        }
    }

    public static final int round(final float x) {
        if (Settings.FAST_MATH) {
            return floor(x + .5f);
        } else {
            return Math.round(x);
        }
    }

    /**
     * Rounds up the value to the nearest higher power^2 value.
     * 
     * @param x
     * @return power^2 value
     */
    public static final int ceilPowerOf2(int x) {
        int pow2 = 1;
        while (pow2 < x) {
            pow2 <<= 1;
        }
        return pow2;
    }

    public final static float max(final float a, final float b) {
        return a > b ? a : b;
    }

    public final static int max(final int a, final int b) {
        return a > b ? a : b;
    }

    public final static float min(final float a, final float b) {
        return a < b ? a : b;
    }

    public final static float map(final float val, final float fromMin,
            final float fromMax, final float toMin, final float toMax) {
        final float mult = (val - fromMin) / (fromMax - fromMin);
        final float res = toMin + mult * (toMax - toMin);
        return res;
    }

    /** Returns the closest value to 'a' that is in between 'low' and 'high' */
    public final static float clamp(final float a, final float low,
            final float high) {
        return max(low, min(a, high));
    }

    public final static Vec2 clamp(final Vec2 a, final Vec2 low, final Vec2 high) {
        final Vec2 min = new Vec2();
        min.x = a.x < high.x ? a.x : high.x;
        min.y = a.y < high.y ? a.y : high.y;
        min.x = low.x > min.x ? low.x : min.x;
        min.y = low.y > min.y ? low.y : min.y;
        return min;
    }

    public final static void clampToOut(final Vec2 a, final Vec2 low,
            final Vec2 high, final Vec2 dest) {
        dest.x = a.x < high.x ? a.x : high.x;
        dest.y = a.y < high.y ? a.y : high.y;
        dest.x = low.x > dest.x ? low.x : dest.x;
        dest.y = low.y > dest.y ? low.y : dest.y;
    }

    /**
     * Next Largest Power of 2: Given a binary integer value x, the next largest
     * power of 2 can be computed by a SWAR algorithm that recursively "folds"
     * the upper bits into the lower bits. This process yields a bit vector with
     * the same most significant 1 as x, but all 1's below it. Adding 1 to that
     * value yields the next largest power of 2.
     */
    public final static int nextPowerOfTwo(int x) {
        x |= x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        x |= x >> 16;
        return x + 1;
    }

    public final static boolean isPowerOfTwo(final int x) {
        return x > 0 && (x & x - 1) == 0;
    }

    public static final float fastPow(float a, float b) {
        return (float) Math.pow((double) a, (double) b);
        // TODO(pruggia): reenable fast pow ?
        // JBOX2DCODE:
        // float x = Float.floatToRawIntBits(a);
        // x *= INV_SHIFT23;
        // x -= 127;
        // float y = x - (x >= 0 ? (int) x : (int) x - 1);
        // b *= x + (y - y * y) * 0.346607f;
        // y = b - (b >= 0 ? (int) b : (int) b - 1);
        // y = (y - y * y) * 0.33971f;
        // return Float.intBitsToFloat((int) ((b + 127 - y) * SHIFT23));
    }

    public static final float atan2(final float y, final float x) {
        if (Settings.FAST_MATH) {
            return fastAtan2(y, x);
        } else {
            return (float) Math.atan2(y, x);
        }
    }

    public static final float fastAtan2(float y, float x) {
        if (x == 0.0f) {
            if (y > 0.0f) return HALF_PI;
            if (y == 0.0f) return 0.0f;
            return -HALF_PI;
        }
        float atan;
        final float z = y / x;
        if (abs(z) < 1.0f) {
            atan = z / (1.0f + 0.28f * z * z);
            if (x < 0.0f) {
                if (y < 0.0f) return atan - PI;
                return atan + PI;
            }
        } else {
            atan = HALF_PI - z / (z * z + 0.28f);
            if (y < 0.0f) return atan - PI;
        }
        return atan;
    }

    public static final float reduceAngle(float theta) {
        theta %= TWOPI;
        if (abs(theta) > PI) {
            theta = theta - TWOPI;
        }
        if (abs(theta) > HALF_PI) {
            theta = PI - theta;
        }
        return theta;
    }

    /**
     * Computes a fast approximation to <code>Math.pow(a, b)</code>. Adapted
     * from <url>http://www.dctsystems.co.uk/Software/power.html</url>.
     * 
     * @param a
     *            a positive number
     * @param b
     *            a number
     * @return a^b
     */
    // UNTESTED
    public static final float pow(final float a, float b) {
        return (float) Math.pow((double) a, (double) b);
        // TODO(pruggia) reenable fast pow ?
        // JBOX2DCODE:
        // // adapted from: http://www.dctsystems.co.uk/Software/power.html
        // if (Settings.FAST_MATH) {
        // float x = Float.floatToRawIntBits(a);
        // x *= 1.0f / (1 << 23);
        // x = x - 127;
        // float y = x - MathUtils.floor(x);
        // b *= x + (y - y * y) * 0.346607f;
        // y = b - MathUtils.floor(b);
        // y = (y - y * y) * 0.33971f;
        // return Float.intBitsToFloat((int) ((b + 127 - y) * (1 << 23)));
        // }
        // else {
        // return (float) Math.pow(a, b);
        // }
    }

    public static final float randomFloat(float argLow, float argHigh) {
        return (float) Math.random() * (argHigh - argLow) + argLow;
    }

    public static final float randomFloat(Random r, float argLow, float argHigh) {
        return r.nextFloat() * (argHigh - argLow) + argLow;
    }

    public static final float sqrt(float x) {
        return (float) Math.sqrt(x);
    }

    public final static float distanceSquared(Vec2 v1, Vec2 v2) {
        float dx = (v1.x - v2.x);
        float dy = (v1.y - v2.y);
        return dx * dx + dy * dy;
    }

    public final static float distance(Vec2 v1, Vec2 v2) {
        return sqrt(distanceSquared(v1, v2));
    }
}
// SINCOS accuracy and speed chart
//
// Tables: 200
// Most Precise Table: 1.0E-5
// Least Precise Table: 0.01
// Accuracy Iterations: 1000000
// Speed Trials: 20
// Speed Iterations: 10000
// constructing tables
// doing accuracy tests
// Accuracy results, average displacement
// Table precision Not lerped Lerped Difference
// 9.99999E-6 1.59338E-6 6.33411E-8 1.53004E-6
// 5.99499E-5 9.52019E-6 5.42142E-8 9.46598E-6
// 1.09899E-4 1.75029E-5 5.53918E-8 1.74475E-5
// 1.59850E-4 2.54499E-5 5.99911E-8 2.53899E-5
// 2.09799E-4 3.33762E-5 5.96989E-8 3.33165E-5
// 2.59749E-4 4.13445E-5 5.60582E-8 4.12885E-5
// 3.09700E-4 4.92908E-5 6.12737E-8 4.92296E-5
// 3.59650E-4 5.72404E-5 5.88096E-8 5.71816E-5
// 4.09599E-4 6.50985E-5 6.21300E-8 6.50363E-5
// 4.59550E-4 7.31303E-5 5.78407E-8 7.30725E-5
// 5.09500E-4 8.10196E-5 5.92192E-8 8.09603E-5
// 5.59450E-4 8.90484E-5 6.07958E-8 8.89876E-5
// 6.09400E-4 9.69890E-5 7.20343E-8 9.69170E-5
// 6.59350E-4 1.04928E-4 6.93258E-8 1.04858E-4
// 7.09300E-4 1.13019E-4 7.11427E-8 1.12947E-4
// 7.59250E-4 1.20989E-4 7.06747E-8 1.20918E-4
// 8.09199E-4 1.28859E-4 7.89727E-8 1.28780E-4
// 8.59150E-4 1.36890E-4 7.43000E-8 1.36815E-4
// 9.09100E-4 1.44773E-4 8.13186E-8 1.44692E-4
// 9.59050E-4 1.52582E-4 8.60978E-8 1.52496E-4
// 0.00100899 1.60769E-4 8.80632E-8 1.60681E-4
// 0.00105894 1.68485E-4 9.66496E-8 1.68389E-4
// 0.00110889 1.76573E-4 9.58513E-8 1.76478E-4
// 0.00115885 1.84327E-4 1.06905E-7 1.84220E-4
// 0.00120880 1.92196E-4 1.19286E-7 1.92077E-4
// 0.00125874 2.00460E-4 1.28806E-7 2.00331E-4
// 0.00130869 2.08042E-4 1.19392E-7 2.07923E-4
// 0.00135865 2.16544E-4 1.44530E-7 2.16399E-4
// 0.00140860 2.24021E-4 1.49854E-7 2.23871E-4
// 0.00145854 2.31827E-4 1.54289E-7 2.31673E-4
// 0.00150850 2.40446E-4 1.44934E-7 2.40301E-4
// 0.00155845 2.47795E-4 1.76676E-7 2.47619E-4
// 0.00160839 2.56248E-4 1.82030E-7 2.56066E-4
// 0.00165834 2.64036E-4 1.87004E-7 2.63849E-4
// 0.00170829 2.71632E-4 1.70919E-7 2.71461E-4
// 0.00175824 2.79823E-4 2.13218E-7 2.79610E-4
// 0.00180820 2.87772E-4 2.19380E-7 2.87553E-4
// 0.00185815 2.95340E-4 2.30138E-7 2.95110E-4
// 0.00190809 3.03284E-4 2.31016E-7 3.03052E-4
// 0.00195805 3.12023E-4 2.42544E-7 3.11780E-4
// 0.00200800 3.19148E-4 2.29997E-7 3.18918E-4
// 0.00205795 3.27906E-4 2.43252E-7 3.27663E-4
// 0.00210790 3.35110E-4 2.93011E-7 3.34817E-4
// 0.00215785 3.43163E-4 3.08860E-7 3.42854E-4
// 0.00220780 3.51323E-4 3.00627E-7 3.51023E-4
// 0.00225775 3.59100E-4 3.04569E-7 3.58795E-4
// 0.00230770 3.67002E-4 3.60740E-7 3.66641E-4
// 0.00235765 3.75095E-4 3.06903E-7 3.74788E-4
// 0.00240760 3.83322E-4 3.94474E-7 3.82927E-4
// 0.00245755 3.91025E-4 4.06166E-7 3.90619E-4
// 0.00250750 3.99437E-4 4.16229E-7 3.99021E-4
// 0.00255744 4.07290E-4 4.24778E-7 4.06866E-4
// 0.00260740 4.15094E-4 4.45167E-7 4.14649E-4
// 0.00265734 4.23249E-4 4.65449E-7 4.22784E-4
// 0.00270730 4.30602E-4 4.63318E-7 4.30139E-4
// 0.00275725 4.38493E-4 4.91764E-7 4.38002E-4
// 0.00280720 4.46582E-4 4.58033E-7 4.46123E-4
// 0.00285715 4.55229E-4 4.52032E-7 4.54777E-4
// 0.00290710 4.63309E-4 5.08717E-7 4.62800E-4
// 0.00295704 4.70165E-4 5.64299E-7 4.69601E-4
// 0.00300700 4.78655E-4 5.78840E-7 4.78076E-4
// 0.00305695 4.86344E-4 5.59216E-7 4.85785E-4
// 0.00310690 4.94030E-4 5.77222E-7 4.93453E-4
// 0.00315685 5.02929E-4 5.99904E-7 5.02329E-4
// 0.00320679 5.10313E-4 6.00234E-7 5.09713E-4
// 0.00325675 5.17840E-4 6.19932E-7 5.17220E-4
// 0.00330670 5.26580E-4 5.96121E-7 5.25984E-4
// 0.00335665 5.34068E-4 6.90259E-7 5.33378E-4
// 0.00340660 5.42020E-4 7.11015E-7 5.41309E-4
// 0.00345655 5.50108E-4 7.76143E-7 5.49332E-4
// 0.00350650 5.58001E-4 7.61866E-7 5.57239E-4
// 0.00355645 5.66693E-4 8.23322E-7 5.65870E-4
// 0.00360640 5.73096E-4 7.33265E-7 5.72362E-4
// 0.00365635 5.81372E-4 8.16293E-7 5.80555E-4
// 0.00370630 5.90145E-4 7.89829E-7 5.89355E-4
// 0.00375625 5.98363E-4 9.15203E-7 5.97448E-4
// 0.00380620 6.05866E-4 9.28685E-7 6.04937E-4
// 0.00385614 6.12882E-4 9.08723E-7 6.11974E-4
// 0.00390610 6.21721E-4 9.86004E-7 6.20735E-4
// 0.00395605 6.30258E-4 8.95759E-7 6.29363E-4
// 0.00400600 6.36307E-4 1.00618E-6 6.35301E-4
// 0.00405595 6.44074E-4 8.99361E-7 6.43175E-4
// 0.00410589 6.52858E-4 9.84549E-7 6.51873E-4
// 0.00415585 6.60822E-4 1.03719E-6 6.59785E-4
// 0.00420580 6.70495E-4 1.02555E-6 6.69470E-4
// 0.00425575 6.77049E-4 1.10163E-6 6.75948E-4
// 0.00430570 6.85498E-4 1.07438E-6 6.84424E-4
// 0.00435565 6.93418E-4 1.18165E-6 6.92236E-4
// 0.00440560 7.01235E-4 1.07350E-6 7.00162E-4
// 0.00445555 7.09658E-4 1.09891E-6 7.08559E-4
// 0.00450550 7.17409E-4 1.32089E-6 7.16088E-4
// 0.00455545 7.25054E-4 1.18787E-6 7.23867E-4
// 0.00460540 7.32882E-4 1.22598E-6 7.31656E-4
// 0.00465534 7.40989E-4 1.42403E-6 7.39565E-4
// 0.00470530 7.48101E-4 1.30740E-6 7.46794E-4
// 0.00475525 7.58028E-4 1.32205E-6 7.56706E-4
// 0.00480520 7.64519E-4 1.47233E-6 7.63047E-4
// 0.00485515 7.71333E-4 1.28258E-6 7.70050E-4
// 0.00490510 7.80806E-4 1.37027E-6 7.79436E-4
// 0.00495505 7.88841E-4 1.31033E-6 7.87531E-4
// 0.00500500 7.96140E-4 1.51493E-6 7.94625E-4
// 0.00505495 8.04295E-4 1.41243E-6 8.02883E-4
// 0.00510489 8.12005E-4 1.65101E-6 8.10354E-4
// 0.00515484 8.19689E-4 1.58882E-6 8.18101E-4
// 0.00520480 8.26688E-4 1.49957E-6 8.25188E-4
// 0.00525475 8.36964E-4 1.79928E-6 8.35165E-4
// 0.00530469 8.43189E-4 1.73475E-6 8.41454E-4
// 0.00535465 8.51817E-4 1.74640E-6 8.50071E-4
// 0.00540460 8.59760E-4 1.86507E-6 8.57895E-4
// 0.00545455 8.68007E-4 1.75090E-6 8.66256E-4
// 0.00550450 8.76977E-4 1.89013E-6 8.75087E-4
// 0.00555445 8.83886E-4 1.71558E-6 8.82170E-4
// 0.00560440 8.91885E-4 1.69996E-6 8.90185E-4
// 0.00565434 8.99345E-4 1.78683E-6 8.97559E-4
// 0.00570430 9.06780E-4 2.08313E-6 9.04697E-4
// 0.00575425 9.16952E-4 1.93477E-6 9.15017E-4
// 0.00580420 9.22571E-4 2.10614E-6 9.20465E-4
// 0.00585414 9.31692E-4 1.99170E-6 9.29700E-4
// 0.00590409 9.39484E-4 1.94949E-6 9.37535E-4
// 0.00595405 9.47631E-4 2.03346E-6 9.45597E-4
// 0.00600400 9.54711E-4 2.26698E-6 9.52444E-4
// 0.00605395 9.64223E-4 2.24711E-6 9.61975E-4
// 0.00610390 9.71415E-4 2.24109E-6 9.69174E-4
// 0.00615385 9.79862E-4 2.01206E-6 9.77850E-4
// 0.00620380 9.85571E-4 2.43784E-6 9.83133E-4
// 0.00625375 9.94922E-4 2.52600E-6 9.92396E-4
// 0.00630370 0.00100276 2.54560E-6 0.00100022
// 0.00635365 0.00101061 2.36969E-6 0.00100824
// 0.00640359 0.00101889 2.27830E-6 0.00101661
// 0.00645354 0.00102795 2.68888E-6 0.00102526
// 0.00650350 0.00103515 2.29183E-6 0.00103286
// 0.00655344 0.00104347 2.73511E-6 0.00104073
// 0.00660340 0.00105219 2.72697E-6 0.00104946
// 0.00665335 0.00105808 2.64758E-6 0.00105544
// 0.00670330 0.00106720 2.62618E-6 0.00106457
// 0.00675325 0.00107393 2.77868E-6 0.00107115
// 0.00680320 0.00108390 2.98322E-6 0.00108092
// 0.00685315 0.00109122 2.93528E-6 0.00108829
// 0.00690310 0.00109721 2.64548E-6 0.00109457
// 0.00695304 0.00110478 3.15083E-6 0.00110163
// 0.00700300 0.00111550 2.76067E-6 0.00111274
// 0.00705295 0.00112321 3.05986E-6 0.00112015
// 0.00710290 0.00113068 3.25251E-6 0.00112743
// 0.00715284 0.00113965 3.15467E-6 0.00113650
// 0.00720280 0.00114663 3.05115E-6 0.00114358
// 0.00725275 0.00115164 3.11508E-6 0.00114853
// 0.00730270 0.00116219 3.20649E-6 0.00115898
// 0.00735265 0.00117260 3.43076E-6 0.00116917
// 0.00740260 0.00117605 3.48923E-6 0.00117257
// 0.00745255 0.00118615 2.97748E-6 0.00118317
// 0.00750250 0.00119260 3.50978E-6 0.00118909
// 0.00755245 0.00120218 3.25920E-6 0.00119892
// 0.00760240 0.00120917 3.61763E-6 0.00120555
// 0.00765235 0.00121934 3.13261E-6 0.00121620
// 0.00770229 0.00122512 3.79680E-6 0.00122132
// 0.00775224 0.00123384 3.76315E-6 0.00123008
// 0.00780220 0.00124021 3.54969E-6 0.00123666
// 0.00785215 0.00124851 3.41675E-6 0.00124510
// 0.00790209 0.00125781 3.39248E-6 0.00125441
// 0.00795205 0.00126521 3.44698E-6 0.00126176
// 0.00800199 0.00127389 3.56137E-6 0.00127033
// 0.00805194 0.00128038 3.83032E-6 0.00127655
// 0.00810190 0.00129096 4.15480E-6 0.00128681
// 0.00815184 0.00129471 4.26089E-6 0.00129045
// 0.00820179 0.00130539 3.59202E-6 0.00130180
// 0.00825174 0.00131406 4.19017E-6 0.00130987
// 0.00830169 0.00131918 4.22569E-6 0.00131495
// 0.00835164 0.00132855 4.15087E-6 0.00132440
// 0.00840159 0.00133826 4.32916E-6 0.00133394
// 0.00845155 0.00134554 4.42999E-6 0.00134111
// 0.00850149 0.00135052 3.86824E-6 0.00134665
// 0.00855144 0.00135946 4.69083E-6 0.00135477
// 0.00860140 0.00136739 4.61559E-6 0.00136277
// 0.00865134 0.00137649 4.28238E-6 0.00137221
// 0.00870130 0.00138384 4.06960E-6 0.00137977
// 0.00875124 0.00139290 4.20102E-6 0.00138870
// 0.00880119 0.00140084 4.58835E-6 0.00139625
// 0.00885115 0.00140791 4.76130E-6 0.00140315
// 0.00890109 0.00141798 4.73735E-6 0.00141324
// 0.00895104 0.00142531 4.55069E-6 0.00142076
// 0.00900099 0.00143613 4.31803E-6 0.00143181
// 0.00905094 0.00144260 4.53024E-6 0.00143807
// 0.00910090 0.00144842 5.03281E-6 0.00144338
// 0.00915084 0.00145642 5.40393E-6 0.00145101
// 0.00920080 0.00146572 5.03671E-6 0.00146068
// 0.00925074 0.00147037 4.78116E-6 0.00146559
// 0.00930069 0.00147971 5.53523E-6 0.00147418
// 0.00935065 0.00148841 4.94373E-6 0.00148346
// 0.00940059 0.00149644 5.32814E-6 0.00149112
// 0.00945054 0.00150297 5.51102E-6 0.00149746
// 0.00950049 0.00151124 5.36400E-6 0.00150588
// 0.00955044 0.00152146 5.43741E-6 0.00151602
// 0.00960039 0.00152633 5.75913E-6 0.00152057
// 0.00965034 0.00153369 4.98641E-6 0.00152870
// 0.00970030 0.00154431 6.03356E-6 0.00153828
// 0.00975024 0.00155032 5.84488E-6 0.00154448
// 0.00980020 0.00156000 5.21861E-6 0.00155478
// 0.00985014 0.00156649 5.87368E-6 0.00156061
// 0.00990009 0.00157338 6.38923E-6 0.00156699
// 0.00995005 0.00158487 6.22094E-6 0.00157865
//
// Doing speed tests
// Speed results, iterations per second
// Table precision Not lerped Lerped Difference
// 9.99999E-6 1.212988E7 1.143473E7 695148.0
// 5.99499E-5 1.029751E7 1.138980E7 -1092287.0
// 1.09899E-4 3.757065E7 1.091965E7 2.665100E7
// 1.59850E-4 3.705580E7 1.146008E7 2.559571E7
// 2.09799E-4 3.882527E7 1.151821E7 2.730706E7
// 2.59749E-4 3.927689E7 1.133740E7 2.793949E7
// 3.09700E-4 3.955518E7 1.152108E7 2.803409E7
// 3.59650E-4 3.860077E7 1.133821E7 2.726256E7
// 4.09599E-4 3.857839E7 1.130567E7 2.727272E7
// 4.59550E-4 3.755295E7 1.140718E7 2.614577E7
// 5.09500E-4 3.799789E7 1.158911E7 2.640877E7
// 5.59450E-4 3.804636E7 1.153696E7 2.650939E7
// 6.09400E-4 3.733554E7 1.137089E7 2.596465E7
// 6.59350E-4 3.898913E7 1.060818E7 2.838095E7
// 7.09300E-4 3.895097E7 1.142511E7 2.752586E7
// 7.59250E-4 3.818386E7 1.135579E7 2.682806E7
// 8.09199E-4 3.973571E7 1.152943E7 2.820627E7
// 8.59150E-4 3.966203E7 1.142634E7 2.823568E7
// 9.09100E-4 3.954913E7 1.137365E7 2.817547E7
// 9.59050E-4 3.962797E7 1.140886E7 2.821910E7
// 0.00100899 3.954863E7 1.133789E7 2.821074E7
// 0.00105894 3.961207E7 1.149570E7 2.811637E7
// 0.00110889 3.969774E7 1.128002E7 2.841772E7
// 0.00115885 3.978212E7 1.134445E7 2.843767E7
// 0.00120880 3.981473E7 1.151025E7 2.830447E7
// 0.00125874 3.966584E7 1.153041E7 2.813543E7
// 0.00130869 3.972691E7 1.142137E7 2.830554E7
// 0.00135865 3.978432E7 1.157375E7 2.821056E7
// 0.00140860 3.969332E7 1.136426E7 2.832906E7
// 0.00145854 3.972309E7 1.100793E7 2.871516E7
// 0.00150850 3.980202E7 1.106633E7 2.873568E7
// 0.00155845 3.958305E7 1.148122E7 2.810182E7
// 0.00160839 3.964443E7 1.130897E7 2.833546E7
// 0.00165834 3.969555E7 1.114758E7 2.854796E7
// 0.00170829 3.969663E7 1.136931E7 2.832731E7
// 0.00175824 3.985185E7 1.138785E7 2.846399E7
// 0.00180820 3.972582E7 1.158578E7 2.814004E7
// 0.00185815 3.977380E7 1.143126E7 2.834253E7
// 0.00190809 3.977327E7 1.153139E7 2.824188E7
// 0.00195805 3.985576E7 1.175924E7 2.809651E7
// 0.00200800 3.975390E7 1.129532E7 2.845858E7
// 0.00205795 3.979427E7 1.097670E7 2.881756E7
// 0.00210790 3.978652E7 1.147993E7 2.830659E7
// 0.00215785 3.970983E7 1.180000E7 2.790983E7
// 0.00220780 3.967958E7 1.152855E7 2.815103E7
// 0.00225775 3.959622E7 1.153845E7 2.805777E7
// 0.00230770 3.959839E7 1.152039E7 2.807800E7
// 0.00235765 3.972911E7 1.153069E7 2.819842E7
// 0.00240760 3.962466E7 1.149703E7 2.812763E7
// 0.00245755 3.982470E7 1.138654E7 2.843816E7
// 0.00250750 3.971259E7 1.143460E7 2.827799E7
// 0.00255744 3.779678E7 1.133296E7 2.646381E7
// 0.00260740 3.991298E7 1.147851E7 2.843447E7
// 0.00265734 3.990072E7 1.131849E7 2.858223E7
// 0.00270730 3.983470E7 1.137383E7 2.846086E7
// 0.00275725 3.992131E7 1.135962E7 2.856169E7
// 0.00280720 3.981419E7 1.136120E7 2.845299E7
// 0.00285715 3.983858E7 1.138197E7 2.845661E7
// 0.00290710 3.984522E7 1.156832E7 2.827689E7
// 0.00295704 4.004918E7 1.161054E7 2.843864E7
// 0.00300700 3.998488E7 1.159272E7 2.839215E7
// 0.00305695 3.999268E7 1.159183E7 2.840084E7
// 0.00310690 3.990239E7 1.158475E7 2.831764E7
// 0.00315685 4.000217E7 1.163592E7 2.836625E7
// 0.00320679 3.991911E7 1.160494E7 2.831416E7
// 0.00325675 4.008167E7 1.161449E7 2.846718E7
// 0.00330670 3.999880E7 1.161049E7 2.838831E7
// 0.00335665 4.001950E7 1.158822E7 2.843128E7
// 0.00340660 3.699261E7 1.012927E7 2.686334E7
// 0.00345655 4.003574E7 1.147414E7 2.85616 E7
// 0.00350650 3.944293E7 1.140768E7 2.803525E7
// 0.00355645 3.886007E7 1.143948E7 2.742058E7
// 0.00360640 3.996422E7 1.151733E7 2.844688E7
// 0.00365635 4.006544E7 1.119618E7 2.886926E7
// 0.00370630 3.876011E7 1.114849E7 2.761162E7
// 0.00375625 3.998152E7 1.146527E7 2.851624E7
// 0.00380620 4.008001E7 1.163507E7 2.844494E7
// 0.00385614 2.526210E7 1.146550E7 1.379659E7
// 0.00390610 3.964880E7 1.144501E7 2.820378E7
// 0.00395605 3.989851E7 1.142921E7 2.846929E7
// 0.00400600 3.975673E7 1.146688E7 2.828985E7
// 0.00405595 3.972967E7 1.147970E7 2.824997E7
// 0.00410589 3.942449E7 1.145225E7 2.797224E7
// 0.00415585 3.952786E7 1.138405E7 2.814380E7
// 0.00420580 3.921339E7 1.145559E7 2.775779E7
// 0.00425575 3.939407E7 1.162912E7 2.776495E7
// 0.00430570 3.963675E7 1.138858E7 2.824817E7
// 0.00435565 3.929571E7 1.101262E7 2.828308E7
// 0.00440560 3.755492E7 1.167073E7 2.588419E7
// 0.00445555 3.967081E7 1.134149E7 2.832932E7
// 0.00450550 3.949844E7 1.142488E7 2.807356E7
// 0.00455545 3.967131E7 1.147161E7 2.819969E7
// 0.00460540 3.953931E7 1.152744E7 2.801186E7
// 0.00465534 3.806153E7 1.151409E7 2.654743E7
// 0.00470530 3.966528E7 1.095654E7 2.870873E7
// 0.00475525 3.911213E7 9764338.0 2.93478 E7
// 0.00480520 3.682139E7 1.135475E7 2.546663E7
// 0.00485515 3.878268E7 1.145206E7 2.733061E7
// 0.00490510 3.962413E7 1.144730E7 2.817682E7
// 0.00495505 3.988569E7 1.140382E7 2.848187E7
// 0.00500500 3.966532E7 1.154128E7 2.812403E7
// 0.00505495 3.932646E7 1.135525E7 2.797121E7
// 0.00510489 3.973023E7 1.146642E7 2.826381E7
// 0.00515484 3.9487 E7 9638301.0 2.984869E7
// 0.00520480 3.936487E7 1.145619E7 2.790867E7
// 0.00525475 3.903806E7 1.152489E7 2.751317E7
// 0.00530469 3.911161E7 1.148624E7 2.762536E7
// 0.00535465 3.989463E7 6311543.0 3.358308E7
// 0.00540460 3.720507E7 1.142711E7 2.577795E7
// 0.00545455 3.759285E7 1.061498E7 2.697787E7
// 0.00550450 3.976554E7 1.157998E7 2.818556E7
// 0.00555445 3.714764E7 1.143748E7 2.571016E7
// 0.00560440 3.975560E7 1.165728E7 2.809832E7
// 0.00565434 3.954207E7 1.146086E7 2.808120E7
// 0.00570430 3.997480E7 8339432.0 3.163537E7
// 0.00575425 3.993465E7 1.144017E7 2.849448E7
// 0.00580420 3.944671E7 1.132619E7 2.812051E7
// 0.00585414 4.032495E7 1.147464E7 2.885031E7
// 0.00590409 3.928978E7 1.096288E7 2.832690E7
// 0.00595405 3.984080E7 1.162619E7 2.821461E7
// 0.00600400 3.979707E7 1.123276E7 2.856431E7
// 0.00605395 3.970712E7 1.167111E7 2.803600E7
// 0.00610390 3.988625E7 1.141450E7 2.847175E7
// 0.00615385 3.857478E7 1.159193E7 2.698285E7
// 0.00620380 3.734483E7 1.06283 E7 2.671653E7
// 0.00625375 4.009233E7 1.167649E7 2.841584E7
// 0.00630370 3.971646E7 1.150355E7 2.821291E7
// 0.00635365 4.000944E7 1.166616E7 2.834327E7
// 0.00640359 3.999268E7 1.152308E7 2.84696 E7
// 0.00645354 4.037726E7 1.106659E7 2.931066E7
// 0.00650350 4.031984E7 1.124993E7 2.906991E7
// 0.00655344 4.043771E7 1.157880E7 2.885890E7
// 0.00660340 4.025918E7 1.148233E7 2.877685E7
// 0.00665335 3.855504E7 1.107494E7 2.748010E7
// 0.00670330 4.003965E7 1.109286E7 2.894678E7
// 0.00675325 4.015644E7 1.138803E7 2.876840E7
// 0.00680320 3.849436E7 1.150299E7 2.699136E7
// 0.00685315 3.734721E7 1.154417E7 2.580303E7
// 0.00690310 3.865233E7 1.162732E7 2.702500E7
// 0.00695304 4.007045E7 1.151289E7 2.855756E7
// 0.00700300 3.962907E7 1.141404E7 2.821503E7
// 0.00705295 4.001670E7 1.115045E7 2.886625E7
// 0.00710290 4.000608E7 1.164467E7 2.836140E7
// 0.00715284 3.953113E7 1.151858E7 2.801255E7
// 0.00720280 4.013730E7 1.154780E7 2.858949E7
// 0.00725275 3.982857E7 1.128090E7 2.854766E7
// 0.00730270 3.441000E7 1.117891E7 2.323109E7
// 0.00735265 3.993633E7 1.127043E7 2.866590E7
// 0.00740260 3.826141E7 1.147998E7 2.678143E7
// 0.00745255 3.985519E7 1.168006E7 2.817513E7
// 0.00750250 4.002453E7 1.122501E7 2.879952E7
// 0.00755245 4.017108E7 1.152725E7 2.864383E7
// 0.00760240 3.955083E7 1.117455E7 2.837628E7
// 0.00765235 3.959622E7 1.077040E7 2.882581E7
// 0.00770229 4.021341E7 1.127083E7 2.894258E7
// 0.00775224 4.013672E7 1.122954E7 2.890717E7
// 0.00780220 4.032495E7 1.169021E7 2.863473E7
// 0.00785215 4.007719E7 1.154487E7 2.853232E7
// 0.00790209 3.891023E7 1.154607E7 2.736415E7
// 0.00795205 3.961808E7 1.151492E7 2.810315E7
// 0.00800199 4.009456E7 1.127770E7 2.881686E7
// 0.00805194 4.021115E7 1.177109E7 2.844006E7
// 0.00810190 4.027052E7 1.154980E7 2.872071E7
// 0.00815184 3.971204E7 1.154673E7 2.816531E7
// 0.00820179 4.033405E7 1.149426E7 2.883978E7
// 0.00825174 4.008169E7 1.134144E7 2.874024E7
// 0.00830169 4.026599E7 1.119600E7 2.906999E7
// 0.00835164 4.031584E7 1.117050E7 2.914534E7
// 0.00840159 3.871400E7 1.131670E7 2.739729E7
// 0.00845155 3.993575E7 1.14474 E7 2.848835E7
// 0.00850149 3.998821E7 1.109747E7 2.889074E7
// 0.00855144 4.022637E7 1.125222E7 2.897415E7
// 0.00860140 4.0363 E7 1.120748E7 2.915551E7
// 0.00865134 3.962578E7 1.140164E7 2.822414E7
// 0.00870130 4.017673E7 1.101355E7 2.916317E7
// 0.00875124 4.030113E7 1.114606E7 2.915507E7
// 0.00880119 4.033460E7 1.120691E7 2.912769E7
// 0.00885115 4.022183E7 1.008258E7 3.013924E7
// 0.00890109 3.970488E7 9142277.0 3.056260E7
// 0.00895104 4.016546E7 1.143081E7 2.873465E7
// 0.00900099 4.018854E7 1.169480E7 2.849373E7
// 0.00905094 4.024163E7 1.149980E7 2.874182E7
// 0.00910090 4.031982E7 9861174.0 3.045865E7
// 0.00915084 3.807014E7 1.124030E7 2.682983E7
// 0.00920080 3.625451E7 1.145481E7 2.479969E7
// 0.00925074 4.013957E7 1.150738E7 2.863218E7
// 0.00930069 4.042912E7 1.145931E7 2.896981E7
// 0.00935065 4.039945E7 1.117080E7 2.922865E7
// 0.00940059 4.034598E7 1.138337E7 2.896260E7
// 0.00945054 3.914958E7 1.168921E7 2.746036E7
// 0.00950049 4.020942E7 1.149976E7 2.870966E7
// 0.00955044 4.011481E7 1.149786E7 2.861694E7
// 0.00960039 4.032667E7 1.162940E7 2.869727E7
// 0.00965034 3.892767E7 1.145871E7 2.746895E7
// 0.00970030 4.051375E7 1.145028E7 2.906346E7
// 0.00975024 4.037726E7 1.164960E7 2.872765E7
// 0.00980020 4.047770E7 1.142615E7 2.905155E7
// 0.00985014 4.051839E7 1.146895E7 2.904944E7
// 0.00990009 4.064258E7 1.156954E7 2.907303E7
// 0.00995005 4.022187E7 1.144438E7 2.877749E7
