package nil.nadph.qnotified.util;

import android.view.View;
import nil.nadph.qnotified.config.ConfigManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.regex.Pattern;

import static nil.nadph.qnotified.util.Initiator._QQAppInterface;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;

@SuppressWarnings("rawtypes")
public class DexKit {

    //WARN: NEVER change the index!
    public static final int C_DIALOG_UTIL = 1;
    public static final int C_FACADE = 2;
    public static final int C_FLASH_PIC_HELPER = 3;
    public static final int C_BASE_PIC_DL_PROC = 4;
    public static final int C_ITEM_BUILDER_FAC = 5;
    public static final int C_AIO_UTILS = 6;
    public static final int C_ABS_GAL_SCENE = 7;
    //public static final int C_FAV_EMO_ROAM_HANDLER = 8;
    public static final int C_FAV_EMO_CONST = 9;
    public static final int C_MSG_REC_FAC = 10;
    public static final int C_CONTACT_UTILS = 11;
    public static final int C_VIP_UTILS = 12;
    public static final int C_ARK_APP_ITEM_BUBBLE_BUILDER = 13;
    public static final int C_PNG_FRAME_UTIL = 14;
    public static final int C_PIC_EMOTICON_INFO = 15;
    public static final int C_SIMPLE_UI_UTIL = 16;
    public static final int C_TROOP_GIFT_UTIL = 17;
    public static final int C_TEST_STRUCT_MSG = 18;
    public static final int C_QZONE_MSG_NOTIFY = 19;
    public static final int C_APP_CONSTANTS = 20;

    //the last index
    public static final int DEOBF_NUM = 20;

    @Nullable
    public static Class tryLoadOrNull(int i) {
        Class ret = load(c(i));
        if (ret != null) return ret;
        try {
            ConfigManager cache = ConfigManager.getCache();
            int lastVersion = cache.getIntOrDefault("cache_" + a(i) + "_code", 0);
            if (getHostInfo(getApplication()).versionCode != lastVersion) {
                return null;
            }
            String clzn = cache.getString("cache_" + a(i) + "_class");
            if (clzn == null) return null;
            ret = load(clzn);
            return ret;
        } catch (Exception e) {
            log(e);
            return null;
        }
    }

    @Nullable
    public static Class doFindClass(int i) {
        Class ret = tryLoadOrNull(i);
        if (ret != null) return ret;
        int ver = -1;
        try {
            ver = getHostInfo(getApplication()).versionCode;
        } catch (Throwable ignored) {
        }
        try {
            HashSet<DexMethodDescriptor> methods;
            ConfigManager cache = ConfigManager.getCache();
            DexDeobfReport report = new DexDeobfReport();
            report.target = i;
            report.version = ver;
            methods = e(i, report);
            if (methods == null || methods.size() == 0) {
                report.v("No method candidate found.");
                log("Unable to deobf: " + c(i));
                return null;
            }
            report.v(methods.size() + " methods(es) found: " + methods);
            HashSet<Class> cas = new HashSet<>();
            for (DexMethodDescriptor m : methods) {
                cas.add(load(m.getDeclaringClassName()));
            }
            report.v("belonging to " + cas.size() + " class(es): " + cas);
            if (cas.size() == 1) {
                ret = cas.iterator().next();
            } else {
                ret = a(i, cas, report);
            }
            report.v("Final decision:" + (ret == null ? null : ret.getName()));
            cache.putString("debof_log_" + a(i), report.toString());
            if (ret == null) {
                log("Multiple classes candidates found, none satisfactory.");
                return null;
            }
            cache.putString("cache_" + a(i) + "_class", ret.getName());
            cache.getAllConfig().put("cache_" + a(i) + "_code", getHostInfo(getApplication()).versionCode);
            cache.save();
        } catch (Exception e) {
            log(e);
        }
        return ret;
    }

    public static String a(int i) {
        switch (i) {
            case C_DIALOG_UTIL:
                return "dialog_util";
            case C_FACADE:
                return "facade";
            case C_FLASH_PIC_HELPER:
                return "flash_helper";
            case C_BASE_PIC_DL_PROC:
                return "base_pic_dl_proc";
            case C_ITEM_BUILDER_FAC:
                return "item_builder_fac";
            case C_AIO_UTILS:
                return "aio_utils";
            case C_ABS_GAL_SCENE:
                return "abs_gal_sc";
            case C_FAV_EMO_CONST:
                return "fav_emo_const";
            case C_MSG_REC_FAC:
                return "msg_rec_fac";
            case C_CONTACT_UTILS:
                return "contact_utils";
            case C_VIP_UTILS:
                return "vip_utils";
            case C_ARK_APP_ITEM_BUBBLE_BUILDER:
                return "ark_app_item_bubble_builder";
            case C_PNG_FRAME_UTIL:
                return "png_frame_util";
            case C_PIC_EMOTICON_INFO:
                return "pic_emoticon_info";
            case C_SIMPLE_UI_UTIL:
                return "simple_ui_util";
            case C_TROOP_GIFT_UTIL:
                return "troop_gift_util";
            case C_TEST_STRUCT_MSG:
                return "test_struct_msg";
            case C_QZONE_MSG_NOTIFY:
                return "qzone_msg_notify";
            case C_APP_CONSTANTS:
                return "app_constants";
        }
        throw new IndexOutOfBoundsException("No class index for " + i + ",max = " + DEOBF_NUM);
    }

    public static String c(int i) {
        String ret;
        switch (i) {
            case C_DIALOG_UTIL:
                ret = "com/tencent/mobileqq/utils/DialogUtil";
                break;
            case C_FACADE:
                ret = "com/tencent/mobileqq/activity/ChatActivityFacade";
                break;
            case C_FLASH_PIC_HELPER:
                ret = "com.tencent.mobileqq.app.FlashPicHelper";
                break;
            case C_BASE_PIC_DL_PROC:
                ret = "com/tencent/mobileqq/transfile/BasePicDownloadProcessor";
                break;
            case C_ITEM_BUILDER_FAC:
                ret = "com/tencent/mobileqq/activity/aio/item/ItemBuilderFactory";
                break;
            case C_AIO_UTILS:
                ret = "com.tencent.mobileqq.activity.aio.AIOUtils";
                break;
            case C_ABS_GAL_SCENE:
                ret = "com/tencent/common/galleryactivity/AbstractGalleryScene";
                break;
            case C_FAV_EMO_CONST:
                ret = "com/tencent/mobileqq/emosm/favroaming/FavEmoConstant";
                break;
            case C_MSG_REC_FAC:
                ret = "com/tencent/mobileqq/service/message/MessageRecordFactory";
                break;
            case C_CONTACT_UTILS:
                ret = "com/tencent/mobileqq/utils/ContactUtils";
                break;
            case C_VIP_UTILS:
                ret = "com/tencent/mobileqq/utils/VipUtils";
                break;
            case C_ARK_APP_ITEM_BUBBLE_BUILDER:
                ret = "com/tencent/mobileqq/activity/aio/item/ArkAppItemBubbleBuilder";
                break;
            case C_PNG_FRAME_UTIL:
                ret = "com.tencent.mobileqq.magicface.drawable.PngFrameUtil";
                break;
            case C_PIC_EMOTICON_INFO:
                ret = "com.tencent.mobileqq.emoticonview.PicEmoticonInfo";
                break;
            case C_SIMPLE_UI_UTIL:
                //dummy, placeholder, just a guess
                ret = "com.tencent.mobileqq.theme.SimpleUIUtil";
                break;
            case C_TROOP_GIFT_UTIL:
                ret = "com/tencent/mobileqq/troop/utils/TroopGiftUtil";
                break;
            case C_TEST_STRUCT_MSG:
                ret = "com/tencent/mobileqq/structmsg/TestStructMsg";
                break;
            case C_QZONE_MSG_NOTIFY:
                ret = "cooperation/qzone/push/MsgNotification";
                break;
            case C_APP_CONSTANTS:
                ret = "com.tencent.mobileqq.app.AppConstants";
                break;
            default:
                ret = null;
        }
        if (ret != null) {
            return ret.replace("/", ".");
        }
        throw new IndexOutOfBoundsException("No class index for " + i + ",max = " + DEOBF_NUM);
    }

    public static byte[][] b(int i) {
        switch (i) {
            case C_DIALOG_UTIL:
                return new byte[][]{new byte[]{0x1B, 0x61, 0x6E, 0x64, 0x72, 0x6F, 0x69, 0x64, 0x2E, 0x70, 0x65, 0x72, 0x6D, 0x69, 0x73, 0x73, 0x69, 0x6F, 0x6E, 0x2E, 0x53, 0x45, 0x4E, 0x44, 0x5F, 0x53, 0x4D, 0x53}};
            case C_FACADE:
                return new byte[][]{new byte[]{0x20, 0x72, 0x65, 0x53, 0x65, 0x6E, 0x64, 0x45, 0x6D, 0x6F}};
            case C_FLASH_PIC_HELPER:
                return new byte[][]{new byte[]{0x0E, 0x46, 0x6C, 0x61, 0x73, 0x68, 0x50, 0x69, 0x63, 0x48, 0x65, 0x6C, 0x70, 0x65, 0x72}};
            case C_BASE_PIC_DL_PROC:
                return new byte[][]{new byte[]{0x2C, 0x42, 0x61, 0x73, 0x65, 0x50, 0x69, 0x63, 0x44, 0x6F, 0x77, 0x6E, 0x6C}};
            case C_ITEM_BUILDER_FAC:
                return new byte[][]{new byte[]{0x24, 0x49, 0x74, 0x65, 0x6D, 0x42, 0x75, 0x69, 0x6C, 0x64, 0x65, 0x72, 0x20, 0x69, 0x73, 0x3A, 0x20, 0x44}};
            case C_AIO_UTILS:
                return new byte[][]{new byte[]{0x0D, 0x6F, 0x70, 0x65, 0x6E, 0x41, 0x49, 0x4F, 0x20, 0x62, 0x79, 0x20, 0x4D, 0x54}};
            case C_ABS_GAL_SCENE:
                return new byte[][]{new byte[]{0x16, 0x67, 0x61, 0x6C, 0x6C, 0x65, 0x72, 0x79, 0x20, 0x73, 0x65, 0x74, 0x43, 0x6F, 0x6C, 0x6F, 0x72, 0x20, 0x62, 0x6C}};
            case C_FAV_EMO_CONST:
                return new byte[][]{
                        new byte[]{0x11, 0x68, 0x74, 0x74, 0x70, 0x3A, 0x2F, 0x2F, 0x70, 0x2E, 0x71, 0x70, 0x69, 0x63, 0x2E},
                        new byte[]{0x12, 0x68, 0x74, 0x74, 0x70, 0x73, 0x3A, 0x2F, 0x2F, 0x70, 0x2E, 0x71, 0x70, 0x69, 0x63, 0x2E},
                };
            case C_MSG_REC_FAC:
                return new byte[][]{new byte[]{0x2C, 0x63, 0x72, 0x65, 0x61, 0x74, 0x65, 0x50, 0x69, 0x63, 0x4D, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65}};
            case C_CONTACT_UTILS:
                return new byte[][]{new byte[]{0x07, 0x20, 0x2D, 0x20, 0x57, 0x69, 0x46, 0x69}};
            case C_VIP_UTILS:
                return new byte[][]{new byte[]{0x05, 0x6A, 0x68, 0x61, 0x6E, 0x5F}};
            case C_ARK_APP_ITEM_BUBBLE_BUILDER:
                return new byte[][]{new byte[]{0x0F, 0x64, 0x65, 0x62, 0x75, 0x67, 0x41, 0x72, 0x6B, 0x4D, 0x65, 0x74, 0x61, 0x20, 0x3D, 0x20}};
            case C_PNG_FRAME_UTIL:
                return new byte[][]{new byte[]{0x2A, 0x66, 0x75, 0x6E, 0x63, 0x20, 0x63, 0x68, 0x65, 0x63, 0x6B, 0x52, 0x61, 0x6E, 0x64, 0x6F, 0x6D, 0x50, 0x6E, 0x67, 0x45, 0x78}};
            case C_PIC_EMOTICON_INFO:
                return new byte[][]{new byte[]{0x20, 0x73, 0x65, 0x6E, 0x64, 0x20, 0x65, 0x6D, 0x6F, 0x74, 0x69, 0x6F, 0x6E, 0x20, 0x2B, 0x20, 0x31, 0x3A}};
            case C_SIMPLE_UI_UTIL:
                return new byte[][]{new byte[]{0x15, 0x6B, 0x65, 0x79, 0x5F, 0x73, 0x69, 0x6D, 0x70, 0x6C, 0x65, 0x5F, 0x73, 0x74, 0x61, 0x74, 0x75, 0x73, 0x5F, 0x73}};
            case C_TROOP_GIFT_UTIL:
                return new byte[][]{new byte[]{0x1A, 0x2E, 0x74, 0x72, 0x6F, 0x6F, 0x70, 0x2E, 0x73, 0x65, 0x6E, 0x64, 0x5F, 0x67, 0x69, 0x66, 0x74, 0x54}};
            case C_TEST_STRUCT_MSG:
                return new byte[][]{new byte[]{0x0D, 0x54, 0x65, 0x73, 0x74, 0x53, 0x74, 0x72, 0x75, 0x63, 0x74, 0x4D, 0x73, 0x67}};
            case C_QZONE_MSG_NOTIFY:
                return new byte[][]{new byte[]{0x14, 0x75, 0x73, 0x65, 0x20, 0x73, 0x6D, 0x61, 0x6C, 0x6C, 0x20, 0x69, 0x63, 0x6F, 0x6E, 0x20, 0x2C, 0x65, 0x78, 0x70, 0x3A}};
            case C_APP_CONSTANTS:
                return new byte[][]{new byte[]{0x0B, 0x2E, 0x69, 0x6E, 0x64, 0x69, 0x76, 0x41, 0x6E, 0x69, 0x6D, 0x2F}};
        }
        throw new IndexOutOfBoundsException("No class index for " + i + ",max = " + DEOBF_NUM);
    }

    public static int[] d(int i) {
        switch (i) {
            case C_DIALOG_UTIL:
                return new int[]{4, 3};
            case C_FACADE:
                return new int[]{6, 3};
            case C_FLASH_PIC_HELPER:
                return new int[]{1, 3};
            case C_BASE_PIC_DL_PROC:
                return new int[]{7, 2};
            case C_ITEM_BUILDER_FAC:
                return new int[]{11, 6, 1};
            case C_AIO_UTILS:
                return new int[]{11, 6};
            case C_ABS_GAL_SCENE:
                return new int[]{1};
            case C_FAV_EMO_CONST:
                return new int[]{4, 5};
            case C_MSG_REC_FAC:
                return new int[]{4};
            case C_CONTACT_UTILS:
                return new int[]{4};
            case C_VIP_UTILS:
                return new int[]{4, 2, 3};
            case C_ARK_APP_ITEM_BUBBLE_BUILDER:
                return new int[]{11, 6};
            case C_PNG_FRAME_UTIL:
                return new int[]{2};
            case C_PIC_EMOTICON_INFO:
                return new int[]{4};
            case C_SIMPLE_UI_UTIL:
                return new int[]{2};
            case C_TROOP_GIFT_UTIL:
                return new int[]{9, 2};
            case C_TEST_STRUCT_MSG:
                return new int[]{7, 2};
            case C_QZONE_MSG_NOTIFY:
                return new int[]{3};
            case C_APP_CONSTANTS:
                return new int[]{1};
        }
        throw new IndexOutOfBoundsException("No class index for " + i + ",max = " + DEOBF_NUM);
    }

    private static Class a(int i, HashSet<Class> __classes, DexDeobfReport report) {
        HashSet<Class> classes = new HashSet<>(__classes);
        for (Class c : __classes) {
            if (c == null || c.getName().contains(".")) {
                classes.remove(c);
            }
        }
        switch (i) {
            case C_DIALOG_UTIL:
            case C_FACADE:
            case C_AIO_UTILS:
            case C_CONTACT_UTILS:
            case C_MSG_REC_FAC:
            case C_VIP_UTILS:
            case C_SIMPLE_UI_UTIL:
            case C_TROOP_GIFT_UTIL:
            case C_TEST_STRUCT_MSG:
                a:
                for (Class clz : classes) {
                    if (Modifier.isAbstract(clz.getModifiers())) continue;
                    for (Field f : clz.getDeclaredFields()) {
                        if (!Modifier.isStatic(f.getModifiers())) continue a;
                    }
                    return clz;
                }
                break;
            case C_FLASH_PIC_HELPER:
                a:
                for (Class clz : classes) {
                    if (Modifier.isAbstract(clz.getModifiers())) continue;
                    for (Field f : clz.getDeclaredFields()) {
                        if (!Modifier.isStatic(f.getModifiers())) continue a;
                    }
                    if (clz.getDeclaredMethods().length > 8) continue;
                    return clz;
                }
                break;
            case C_BASE_PIC_DL_PROC:
                for (Class clz : classes) {
                    for (Field f : clz.getDeclaredFields()) {
                        int m = f.getModifiers();
                        if (Modifier.isStatic(m) && Modifier.isFinal(m) && f.getType().equals(Pattern.class))
                            return clz;
                    }
                }
                break;
            case C_ITEM_BUILDER_FAC:
                for (Class clz : classes) {
                    if (clz.getDeclaredFields().length > 30) return clz;
                }
                break;
            case C_ABS_GAL_SCENE:
                for (Class clz : classes) {
                    if (!Modifier.isAbstract(clz.getModifiers())) continue;
                    for (Field f : clz.getDeclaredFields()) {
                        if (f.getType().equals(View.class))
                            return clz;
                    }
                }
                break;
            case C_FAV_EMO_CONST:
                a:
                for (Class clz : classes) {
                    if (Modifier.isAbstract(clz.getModifiers())) continue;
                    for (Field f : clz.getDeclaredFields()) {
                        if (!Modifier.isStatic(f.getModifiers())) continue a;
                    }
                    if (clz.getDeclaredMethods().length > 3) continue;
                    return clz;
                }
                break;
            case C_ARK_APP_ITEM_BUBBLE_BUILDER:
                for (Class clz : classes) {
                    if (Modifier.isAbstract(clz.getModifiers())) continue;
                    Class sp = clz.getSuperclass();
                    if (Object.class.equals(sp)) continue;
                    if (!Modifier.isAbstract(sp.getModifiers())) continue;
                    if (sp.getName().contains("Builder")) return clz;
                    return clz;
                }
                break;
            case C_PNG_FRAME_UTIL:
                for (Class clz : classes) {
                    for (Method m : clz.getMethods()) {
                        if (m.getName().equals("b")) continue;
                        if (!m.getReturnType().equals(int.class)) continue;
                        if (!Modifier.isStatic(m.getModifiers())) continue;
                        Class[] argt = m.getParameterTypes();
                        if (argt.length == 1 && int.class.equals(argt[0])) return clz;
                    }
                    return clz;
                }
                break;
            case C_PIC_EMOTICON_INFO:
                for (Class clz : classes) {
                    if (Modifier.isAbstract(clz.getModifiers())) continue;
                    Class s = clz.getSuperclass();
                    if (Object.class.equals(s)) continue;
                    s = s.getSuperclass();
                    if (Object.class.equals(s)) continue;
                    s = s.getSuperclass();
                    if (Object.class.equals(s)) return clz;
                }
                break;
            case C_QZONE_MSG_NOTIFY:
                for (Class clz : classes) {
                    if (Modifier.isAbstract(clz.getModifiers())) continue;
                    Class s = clz.getSuperclass();
                    if (!Object.class.equals(s)) continue;
                    for (Method m : clz.getDeclaredMethods()) {
                        if (!m.getReturnType().equals(void.class)) continue;
                        Class<?>[] argt = m.getParameterTypes();
                        if (argt.length > 7 && argt[0].equals(_QQAppInterface())) {
                            return clz;
                        }
                    }
                }
                break;
            case C_APP_CONSTANTS:
                for (Class clz : classes) {
                    if (!Modifier.isInterface(clz.getModifiers())) continue;
                    if (clz.getDeclaredFields().length < 50) continue;
                    return clz;
                }
                break;
        }
        return null;
    }

    @Nullable
    private static HashSet<DexMethodDescriptor> e(int i, DexDeobfReport rep) {
        ClassLoader loader = Initiator.getHostClassLoader();
        int record = 0;
        int[] qf = d(i);
        byte[][] keys = b(i);
        if (qf != null) for (int dexi : qf) {
            record |= 1 << dexi;
            try {
                for (byte[] k : keys) {
                    HashSet<DexMethodDescriptor> ret = findMethodsByConstString(k, dexi, loader);
                    if (ret != null && ret.size() > 0) return ret;
                }
            } catch (FileNotFoundException ignored) {
            }
        }
        int dexi = 1;
        while (true) {
            if ((record & (1 << dexi)) != 0) {
                dexi++;
                continue;
            }
            try {
                for (byte[] k : keys) {
                    HashSet<DexMethodDescriptor> ret = findMethodsByConstString(k, dexi, loader);
                    if (ret != null && ret.size() > 0) return ret;
                }
            } catch (FileNotFoundException ignored) {
                return null;
            }
            dexi++;
        }
    }

    /**
     * get ALL the possible class names
     *
     * @param key    the pattern
     * @param i      C_XXXX
     * @param loader to get dex file
     * @return ["abc","ab"]
     * @throws FileNotFoundException apk has no classesN.dex
     */
    public static HashSet<DexMethodDescriptor> findMethodsByConstString(byte[] key, int i, ClassLoader loader) throws FileNotFoundException {
        String name;
        byte[] buf = new byte[4096];
        byte[] content;
        if (i == 1) name = "classes.dex";
        else name = "classes" + i + ".dex";
        Enumeration<URL> urls = null;
        try {
            urls = (Enumeration<URL>) Utils.invoke_virtual(loader, "findResources", name, String.class);
        } catch (Throwable e) {
            log(e);
        }
        //log("dex" + i + ":" + url);
        if (urls == null || !urls.hasMoreElements()) throw new FileNotFoundException(name);
        InputStream in;
        try {
            HashSet<DexMethodDescriptor> rets = new HashSet<>();
            while (urls.hasMoreElements()) {
                in = urls.nextElement().openStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int ii;
                while ((ii = in.read(buf)) != -1) {
                    baos.write(buf, 0, ii);
                }
                in.close();
                content = baos.toByteArray();
				/*if (i == 1) {
					log("dex" + i + ".len :" + content.length);
				}*/
                ArrayList<Integer> opcodeOffsets = a(content, key);
                for (int j = 0; j < opcodeOffsets.size(); j++) {
                    try {
                        DexMethodDescriptor desc = getDexMethodByOpOffset(content, opcodeOffsets.get(j), true);
                        if (desc != null) rets.add(desc);
                    } catch (InternalError ignored) {
                    }
                }
            }
            return rets;
        } catch (IOException e) {
            log(e);
            return null;
        }
    }

    public static ArrayList<Integer> a(byte[] buf, byte[] target) {
        ArrayList<Integer> rets = new ArrayList<>();
        int[] ret = new int[1];
        final float[] f = new float[1];
        ret[0] = arrayIndexOf(buf, target, 0, buf.length, f);
        ret[0] = arrayIndexOf(buf, int2u4le(ret[0]), 0, buf.length, f);
        //System.out.println(ret[0]);
        int strIdx = (ret[0] - readLe32(buf, 0x3c)) / 4;
        if (strIdx > 0xFFFF) {
            target = int2u4le(strIdx);
        } else target = int2u2le(strIdx);
        int off = 0;
        while (true) {
            off = arrayIndexOf(buf, target, off + 1, buf.length, f);
            if (off == -1) break;
            if (buf[off - 2] == (byte) 26/*Opcodes.OP_CONST_STRING*/
                    || buf[off - 2] == (byte) 27)/* Opcodes.OP_CONST_STRING_JUMBO*/ {
                ret[0] = off - 2;
                int opcodeOffset = ret[0];
                if (buf[off - 2] == (byte) 27 && strIdx < 0x10000) {
                    if (readLe32(buf, opcodeOffset + 2) != strIdx) continue;
                }
                rets.add(opcodeOffset);
            }
        }
        return rets;
    }

    public static class DexMethodDescriptor {
        /**
         * Ljava/lang/Object;
         */
        public String declaringClass;
        /**
         * toString
         */
        public String methodName;
        /**
         * ()Ljava/lang/String;
         */
        public String signature;

        public DexMethodDescriptor(String clz, String n, String s) {
            if (clz == null || n == null || s == null) throw new NullPointerException();
            declaringClass = clz;
            methodName = n;
            signature = s;
        }

        public String getDeclaringClassName() {
            return declaringClass.substring(1, declaringClass.length() - 1).replace('/', '.');
        }

        @Override
        public String toString() {
            return declaringClass + "->" + methodName + signature;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return toString().equals(o.toString());
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }
    }

    /**
     * @param buf       the byte array containing the whole dex file
     * @param opcodeOff offset relative to {@code buf}
     * @param verify    whether to verify if the {@code opcodeOff} is aligned to opcode,
     *                  return {@code null} if the offset failed the verification
     * @return
     */
    @Nullable
    public static DexMethodDescriptor getDexMethodByOpOffset(byte[] buf, int opcodeOff, boolean verify) {
        int methodIdsSize = readLe32(buf, 0x58);
        int methodIdsOff = readLe32(buf, 0x5c);
        int classDefsSize = readLe32(buf, 0x60);
        int classDefsOff = readLe32(buf, 0x64);
        int[] p = new int[1];
        int[] ret = new int[1];
        int[] co = new int[1];
        for (int cn = 0; cn < classDefsSize; cn++) {
            int classIdx = readLe32(buf, classDefsOff + cn * 32);
            int classDataOff = readLe32(buf, classDefsOff + cn * 32 + 24);
            p[0] = classDataOff;
            if (classDataOff == 0) continue;
            int fieldIdx = 0;
            int staticFieldsSize = readUleb128(buf, p),
                    instanceFieldsSize = readUleb128(buf, p),
                    directMethodsSize = readUleb128(buf, p),
                    virtualMethodsSize = readUleb128(buf, p);
            for (int fn = 0; fn < staticFieldsSize + instanceFieldsSize; fn++) {
                fieldIdx += readUleb128(buf, p);
                int accessFlags = readUleb128(buf, p);
            }
            int methodIdx = 0;
            for (int mn = 0; mn < directMethodsSize; mn++) {
                methodIdx += readUleb128(buf, p);
                int accessFlags = readUleb128(buf, p);
                int codeOff = co[0] = readUleb128(buf, p);
                if (codeOff == 0) continue;
                int insnsSize = readLe32(buf, codeOff + 12);
                if (codeOff + 16 <= opcodeOff && opcodeOff <= codeOff + 16 + insnsSize * 2) {
                    if (verify && !verifyOpcodeOffset(buf, codeOff + 16, insnsSize * 2, opcodeOff)) {
                        return null;
                    }
                    String clz = readType(buf, classIdx);
                    int pMethodId = methodIdsOff + 8 * methodIdx;
                    String name = readString(buf, readLe32(buf, pMethodId + 4));
                    String sig = readProto(buf, readLe16(buf, pMethodId + 2));
                    return new DexMethodDescriptor(clz, name, sig);
                }
            }
            methodIdx = 0;
            for (int mn = 0; mn < virtualMethodsSize; mn++) {
                methodIdx += readUleb128(buf, p);
                int accessFlags = readUleb128(buf, p);
                int codeOff = co[0] = readUleb128(buf, p);
                if (codeOff == 0) continue;
                int insnsSize = readLe32(buf, codeOff + 12);
                if (codeOff + 16 <= opcodeOff && opcodeOff <= codeOff + 16 + insnsSize * 2) {
                    if (verify && !verifyOpcodeOffset(buf, codeOff + 16, insnsSize * 2, opcodeOff)) {
                        return null;
                    }
                    String clz = readType(buf, classIdx);
                    int pMethodId = methodIdsOff + 8 * methodIdx;
                    String name = readString(buf, readLe32(buf, pMethodId + 4));
                    String sig = readProto(buf, readLe16(buf, pMethodId + 2));
                    return new DexMethodDescriptor(clz, name, sig);
                }
            }
        }
        return null;
    }

    public static int readUleb128(byte[] src, int[] offset) {
        int result = 0;
        int count = 0;
        int cur;
        do {
            cur = src[offset[0]];
            cur &= 0xff;
            result |= (cur & 0x7f) << count * 7;
            count++;
            offset[0]++;
        } while ((cur & 0x80) == 128 && count < 5);
        return result;
    }

    public static String readString(byte[] buf, int idx) {
        int stringIdsOff = readLe32(buf, 0x3c);
        int strOff = readLe32(buf, stringIdsOff + 4 * idx);
        int len = buf[strOff];//hack,just assume it no longer than 127
        return new String(buf, strOff + 1, len);
    }

    public static String readType(byte[] buf, int idx) {
        int typeIdsOff = readLe32(buf, 0x44);
        int strIdx = readLe32(buf, typeIdsOff + 4 * idx);
        return readString(buf, strIdx);
    }

    public static String readProto(byte[] buf, int idx) {
        int protoIdsOff = readLe32(buf, 0x4c);
        //int shortyStrIdx = readLe32(buf, protoIdsOff + 12 * idx);
        int returnTypeIdx = readLe32(buf, protoIdsOff + 12 * idx + 4);
        int parametersOff = readLe32(buf, protoIdsOff + 12 * idx + 8);
        StringBuilder sb = new StringBuilder("(");
        if (parametersOff != 0) {
            int size = readLe32(buf, parametersOff);
            for (int i = 0; i < size; i++) {
                int typeIdx = readLe16(buf, parametersOff + 4 + 2 * i);
                sb.append(readType(buf, typeIdx));
            }
        }
        sb.append(")");
        sb.append(readType(buf, returnTypeIdx));
        return sb.toString();
    }

    public static int arrayIndexOf(byte[] arr, byte[] subarr, int startindex, int endindex, float[] progress) {
        byte a = subarr[0];
        float d = endindex - startindex;
        int b = endindex - subarr.length;
        int i = startindex;
        int ii;
        a:
        while (i <= b) {
            if (arr[i] != a) {
                progress[0] = (i++ - startindex) / d;
                continue;
            } else {
                for (ii = 0; ii < subarr.length; ii++) {
                    if (arr[i++] != subarr[ii]) {
                        i = i - ii;
                        continue a;
                    }
                }
                return i - ii;
            }
        }
        return -1;
    }

    public static byte[] int2u4le(int i) {
        return new byte[]{(byte) i, (byte) (i >> 8), (byte) (i >> 16), (byte) (i >> 24)};
    }

    public static byte[] int2u2le(int i) {
        return new byte[]{(byte) i, (byte) (i >> 8)};
    }

    public static int readLe32(byte[] buf, int index) {
        int i = buf[index] & 0xFF | (buf[index + 1] << 8) & 0xff00 | (buf[index + 2] << 16) & 0xff0000 | (buf[index + 3] << 24) & 0xff000000;
        return i;
    }

    public static int readLe16(byte[] buf, int off) {
        int i = (buf[off] & 0xFF) | ((buf[off + 1] << 8) & 0xff00);
        return i;
    }

    public static class DexDeobfReport {
        int target;
        int version;
        String result;
        String log;
        long time;

        public DexDeobfReport() {
            time = System.currentTimeMillis();
        }

        public void v(String str) {
            if (log == null) log = str + "\n";
            else log = log + str + "\n";
        }

        @Override
        public String toString() {
            return "Deobf target: " + target + '\n' +
                    "Time: " + time + '\n' +
                    "QQ version code: " + version + '\n' +
                    "Result: " + result + '\n' +
                    log;
        }
    }

    public static boolean verifyOpcodeOffset(byte[] buf, int insStart, int bLen, int opcodeOffset) {
        for (int i = 0; i < bLen; ) {
            if (insStart + i == opcodeOffset) return true;
            int opv = buf[insStart + i] & 0xff;
            int len = OPCODE_LENGTH_TABLE[opv];
            if (len == 0) {
                log(String.format("Unrecognized opcode = 0x%02x", opv));
                return false;
            }
            i += 2 * len;
        }
        return false;
    }

    private static final byte[] OPCODE_LENGTH_TABLE = new byte[]{
            1, 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 2, 3, 2, 2, 3, 5, 2, 2, 3, 2, 1, 1, 2,
            2, 1, 2, 2, 3, 3, 3, 1, 1, 2, 3, 3, 3, 2, 2, 2,
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0,
            0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3,
            3, 3, 3, 1, 3, 3, 3, 3, 3, 0, 0, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3,
            3, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 2, 2, 2, 2};
}
