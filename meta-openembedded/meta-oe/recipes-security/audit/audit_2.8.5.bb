SUMMARY = "User space tools for kernel auditing"
DESCRIPTION = "The audit package contains the user space utilities for \
storing and searching the audit records generated by the audit subsystem \
in the Linux kernel."
HOMEPAGE = "http://people.redhat.com/sgrubb/audit/"
SECTION = "base"
LICENSE = "GPLv2+ & LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = "git://github.com/linux-audit/${BPN}-userspace.git;branch=2.8_maintenance \
           file://0001-Add-substitue-functions-for-strndupa-rawmemchr.patch \
           file://0002-Fixed-swig-host-contamination-issue.patch \
           file://0003-Header-definitions-need-to-be-external-when-building.patch \
           file://auditd \
           file://auditd.service \
           file://audit-volatile.conf \
"

S = "${WORKDIR}/git"
SRCREV = "5fae55c1ad15b3cefe6890eba7311af163e9133c"

inherit autotools python3native update-rc.d systemd

UPDATERCPN = "auditd"
INITSCRIPT_NAME = "auditd"
INITSCRIPT_PARAMS = "defaults"

SYSTEMD_PACKAGES = "auditd"
SYSTEMD_SERVICE:auditd = "auditd.service"

DEPENDS = "python3 tcp-wrappers libcap-ng linux-libc-headers swig-native"

EXTRA_OECONF = "--without-prelude \
        --with-libwrap \
        --enable-gssapi-krb5=no \
        --with-libcap-ng=yes \
        --with-python3=yes \
        --libdir=${base_libdir} \
        --sbindir=${base_sbindir} \
        --without-python \
        --without-golang \
        --disable-zos-remote \
        --with-arm=yes \
        --with-aarch64=yes \
        "

EXTRA_OEMAKE = "PYLIBVER='python${PYTHON_BASEVERSION}' \
	PYINC='${STAGING_INCDIR}/$(PYLIBVER)' \
	pyexecdir=${libdir}/python${PYTHON_BASEVERSION}/site-packages \
	STDINC='${STAGING_INCDIR}' \
	pkgconfigdir=${libdir}/pkgconfig \
	"

SUMMARY:audispd-plugins = "Plugins for the audit event dispatcher"
DESCRIPTION:audispd-plugins = "The audispd-plugins package provides plugins for the real-time \
interface to the audit system, audispd. These plugins can do things \
like relay events to remote machines or analyze events for suspicious \
behavior."

PACKAGES =+ "audispd-plugins"
PACKAGES += "auditd ${PN}-python"

FILES:${PN} = "${sysconfdir}/libaudit.conf ${base_libdir}/libaudit.so.1* ${base_libdir}/libauparse.so.*"
FILES:auditd = "${bindir}/* ${base_sbindir}/* ${sysconfdir}/*"
FILES:audispd-plugins = "${sysconfdir}/audisp/audisp-remote.conf \
	${sysconfdir}/audisp/plugins.d/au-remote.conf \
	${base_sbindir}/audisp-remote ${localstatedir}/spool/audit \
	"
FILES:${PN}-dbg += "${libdir}/python${PYTHON_BASEVERSION}/*/.debug"
FILES:${PN}-python = "${libdir}/python${PYTHON_BASEVERSION}"

CONFFILES:auditd = "${sysconfdir}/audit/audit.rules"
RDEPENDS:auditd = "bash"

do_install:append() {
	rm -f ${D}/${libdir}/python${PYTHON_BASEVERSION}/site-packages/*.a
	rm -f ${D}/${libdir}/python${PYTHON_BASEVERSION}/site-packages/*.la

	# reuse auditd config
	[ ! -e ${D}/etc/default ] && mkdir ${D}/etc/default
	mv ${D}/etc/sysconfig/auditd ${D}/etc/default
	rmdir ${D}/etc/sysconfig/

	# replace init.d
	install -D -m 0755 ${WORKDIR}/auditd ${D}/etc/init.d/auditd
	rm -rf ${D}/etc/rc.d

	if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
		# install systemd unit files
		install -d ${D}${systemd_unitdir}/system
		install -m 0644 ${WORKDIR}/auditd.service ${D}${systemd_unitdir}/system

		install -d ${D}${sysconfdir}/tmpfiles.d/
		install -m 0644 ${WORKDIR}/audit-volatile.conf ${D}${sysconfdir}/tmpfiles.d/
	fi

	# audit-2.5 doesn't install any rules by default, so we do that here
	mkdir -p ${D}/etc/audit ${D}/etc/audit/rules.d
	cp ${S}/rules/10-base-config.rules ${D}/etc/audit/rules.d/audit.rules

	chmod 750 ${D}/etc/audit ${D}/etc/audit/rules.d
	chmod 640 ${D}/etc/audit/auditd.conf ${D}/etc/audit/rules.d/audit.rules

	# Based on the audit.spec "Copy default rules into place on new installation"
	cp ${D}/etc/audit/rules.d/audit.rules ${D}/etc/audit/audit.rules

	# Create /var/spool/audit directory for audisp-remote
	install -m 0700 -d ${D}${localstatedir}/spool/audit
}
