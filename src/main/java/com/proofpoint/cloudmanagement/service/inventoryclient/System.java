/*
 * Copyright 2010 Proofpoint, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.proofpoint.cloudmanagement.service.inventoryclient;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Set;

@JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
class System
{
    private String serialNumber;
    private String systemType;
    private String rackPosition;
    private String operatingSystem;
    private String biosVersion;
    private String auditInfo;
    private String biosVendor;
    private String operatingSystemRelease;
    private String rackCode;
    private String assetTagNumber;
    private String agentReported;
    private String bladeChassisSerial;
    private String ppsClusterId;
    private String diskDriveCount;
    private String roles;
    private String ppsAgents;
    private String cageCode;
    private String warrantyInfo;
    private String ppsCustomerId;
    private String kernelRelease;
    private String raidVolumes;
    private String raidDriveStatus;
    private String fqdn;
    private String dracMacaddress;
    private String changes;
    private String fileSystems;
    private String netdriverVersion;
    private String netdriverFirmware;
    private String guestFqdns;
    private String inventoryComponentType;
    private String customers;
    private String agentType;
    private String svcId;
    private String ppsConfigRole;
    private String hostFqdn;
    private String powerSupplyWatts;
    private String drac;
    private String physicalProcessorCount;
    private String status;
    private String dateCreated;
    private String memorySize;
    private String raidBadDrives;
    private String netdriverDuplex;
    private String macAddress;
    private String primaryInterface;
    private String interfaces;
    private String raidController;
    private String processors;
    private String ipAddress;
    private String raidDrives;
    private String dateModified;
    private String raidType;
    private String hardwareClass;
    private String virtual;
    private String netdriver;
    private String powerSupplyCount;
    private String dataCenterCode;
    private String manufacturer;
    private String dracVersion;
    private String notes;
    private String netdriverSpeed;
    private String productName;

    @JsonProperty("serial_number")
    public String getSerialNumber()
    {
        return serialNumber;
    }

    @JsonProperty("system_type")
    public String getSystemType()
    {
        return systemType;
    }

    @JsonProperty("rack_position")
    public String getRackPosition()
    {
        return rackPosition;
    }

    @JsonProperty("operating_system")
    public String getOperatingSystem()
    {
        return operatingSystem;
    }

    @JsonProperty("bios_version")
    public String getBiosVersion()
    {
        return biosVersion;
    }

    @JsonProperty("audit_info")
    public String getAuditInfo()
    {
        return auditInfo;
    }

    @JsonProperty("bios_vendor")
    public String getBiosVendor()
    {
        return biosVendor;
    }

    @JsonProperty("operating_system_release")
    public String getOperatingSystemRelease()
    {
        return operatingSystemRelease;
    }

    @JsonProperty("rack_code")
    public String getRackCode()
    {
        return rackCode;
    }

    @JsonProperty("asset_tag_number")
    public String getAssetTagNumber()
    {
        return assetTagNumber;
    }

    @JsonProperty("agent_reported")
    public String getAgentReported()
    {
        return agentReported;
    }

    @JsonProperty("blade_chassis_serial")
    public String getBladeChassisSerial()
    {
        return bladeChassisSerial;
    }

    @JsonProperty("pps_clusterid")
    public String getPpsClusterId()
    {
        return ppsClusterId;
    }

    @JsonProperty("disk_drive_count")
    public String getDiskDriveCount()
    {
        return diskDriveCount;
    }

    @JsonProperty("roles")
    public String getRolesAsSerializedString()
    {
        return roles;
    }

    public Set<String> getRoles()
    {
        return Sets.newTreeSet(Splitter.on(',').trimResults().split(roles));
    }

    @JsonProperty("pps_agents")
    public String getPpsAgents()
    {
        return ppsAgents;
    }

    @JsonProperty("cage_code")
    public String getCageCode()
    {
        return cageCode;
    }

    @JsonProperty("warranty_info")
    public String getWarrantyInfo()
    {
        return warrantyInfo;
    }

    @JsonProperty("pps_customerid")
    public String getPpsCustomerId()
    {
        return ppsCustomerId;
    }

    @JsonProperty("kernel_release")
    public String getKernelRelease()
    {
        return kernelRelease;
    }

    @JsonProperty("raidvolumes")
    public String getRaidVolumes()
    {
        return raidVolumes;
    }

    @JsonProperty("raiddrivestatus")
    public String getRaidDriveStatus()
    {
        return raidDriveStatus;
    }

    @JsonProperty("fqdn")
    public String getFqdn()
    {
        return fqdn;
    }

    @JsonProperty("drac_macaddress")
    public String getDracMacaddress()
    {
        return dracMacaddress;
    }

    @JsonProperty("changes")
    public String getChanges()
    {
        return changes;
    }

    @JsonProperty("file_systems")
    public String getFileSystems()
    {
        return fileSystems;
    }

    @JsonProperty("netdriver_version")
    public String getNetdriverVersion()
    {
        return netdriverVersion;
    }

    @JsonProperty("netdriver_firmware")
    public String getNetdriverFirmware()
    {
        return netdriverFirmware;
    }

    @JsonProperty("guest_fqdns")
    public String getGuestFqdns()
    {
        return guestFqdns;
    }

    @JsonProperty("inventory_component_type")
    public String getInventoryComponentType()
    {
        return inventoryComponentType;
    }

    @JsonProperty("customers")
    public String getCustomers()
    {
        return customers;
    }

    @JsonProperty("agent_type")
    public String getAgentType()
    {
        return agentType;
    }

    @JsonProperty("svc_id")
    public String getSvcId()
    {
        return svcId;
    }

    @JsonProperty("pps_config_role")
    public String getPpsConfigRole()
    {
        return ppsConfigRole;
    }

    @JsonProperty("host_fqdn")
    public String getHostFqdn()
    {
        return hostFqdn;
    }

    @JsonProperty("power_supply_watts")
    public String getPowerSupplyWatts()
    {
        return powerSupplyWatts;
    }

    @JsonProperty("drac")
    public String getDrac()
    {
        return drac;
    }

    @JsonProperty("physical_processor_count")
    public String getPhysicalProcessorCount()
    {
        return physicalProcessorCount;
    }

    @JsonProperty("status")
    public String getStatus()
    {
        return status;
    }

    @JsonProperty("date_created")
    public String getDateCreated()
    {
        return dateCreated;
    }

    @JsonProperty("memory_size")
    public String getMemorySize()
    {
        return memorySize;
    }

    @JsonProperty("raidbaddrives")
    public String getRaidBadDrives()
    {
        return raidBadDrives;
    }

    @JsonProperty("netdriver_duplex")
    public String getNetdriverDuplex()
    {
        return netdriverDuplex;
    }

    @JsonProperty("mac_address")
    public String getMacAddress()
    {
        return macAddress;
    }

    @JsonProperty("primary_interface")
    public String getPrimaryInterface()
    {
        return primaryInterface;
    }

    @JsonProperty("interfaces")
    public String getInterfaces()
    {
        return interfaces;
    }

    @JsonProperty("raidcontroller")
    public String getRaidController()
    {
        return raidController;
    }

    @JsonProperty("processors")
    public String getProcessors()
    {
        return processors;
    }

    @JsonProperty("virtual")
    public String getVirtual()
    {
        return virtual;
    }

    @JsonProperty("netdriver")
    public String getNetdriver()
    {
        return netdriver;
    }

    @JsonProperty("power_supply_count")
    public String getPowerSupplyCount()
    {
        return powerSupplyCount;
    }

    @JsonProperty("data_center_code")
    public String getDataCenterCode()
    {
        return dataCenterCode;
    }

    @JsonProperty("manufacturer")
    public String getManufacturer()
    {
        return manufacturer;
    }

    @JsonProperty("drac_version")
    public String getDracVersion()
    {
        return dracVersion;
    }

    @JsonProperty("notes")
    public String getNotes()
    {
        return notes;
    }

    @JsonProperty("netdriver_speed")
    public String getNetdriverSpeed()
    {
        return netdriverSpeed;
    }

    @JsonProperty("product_name")
    public String getProductName()
    {
        return productName;
    }

    @JsonProperty("ip_address")
    public String getIpAddress()
    {
        return ipAddress;
    }

    @JsonProperty("raiddrives")
    public String getRaidDrives()
    {
        return raidDrives;
    }

    @JsonProperty("date_modified")
    public String getDateModified()
    {
        return dateModified;
    }

    @JsonProperty("raidtype")
    public String getRaidType()
    {
        return raidType;
    }

    @JsonProperty("hardware_class")
    public String getHardwareClass()
    {
        return hardwareClass;
    }

    @JsonProperty("serial_number")
    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    @JsonProperty("system_type")
    public void setSystemType(String systemType)
    {
        this.systemType = systemType;
    }

    @JsonProperty("rack_position")
    public void setRackPosition(String rackPosition)
    {
        this.rackPosition = rackPosition;
    }

    @JsonProperty("operating_system")
    public void setOperatingSystem(String operatingSystem)
    {
        this.operatingSystem = operatingSystem;
    }

    @JsonProperty("bios_version")
    public void setBiosVersion(String biosVersion)
    {
        this.biosVersion = biosVersion;
    }

    @JsonProperty("audit_info")
    public void setAuditInfo(String auditInfo)
    {
        this.auditInfo = auditInfo;
    }

    @JsonProperty("bios_vendor")
    public void setBiosVendor(String biosVendor)
    {
        this.biosVendor = biosVendor;
    }

    @JsonProperty("operating_system_release")
    public void setOperatingSystemRelease(String operatingSystemRelease)
    {
        this.operatingSystemRelease = operatingSystemRelease;
    }

    @JsonProperty("rack_code")
    public void setRackCode(String rackCode)
    {
        this.rackCode = rackCode;
    }

    @JsonProperty("asset_tag_number")
    public void setAssetTagNumber(String assetTagNumber)
    {
        this.assetTagNumber = assetTagNumber;
    }

    @JsonProperty("agent_reported")
    public void setAgentReported(String agentReported)
    {
        this.agentReported = agentReported;
    }

    @JsonProperty("blade_chassis_serial")
    public void setBladeChassisSerial(String bladeChassisSerial)
    {
        this.bladeChassisSerial = bladeChassisSerial;
    }

    @JsonProperty("pps_clusterid")
    public void setPpsClusterId(String ppsClusterId)
    {
        this.ppsClusterId = ppsClusterId;
    }

    @JsonProperty("disk_drive_count")
    public void setDiskDriveCount(String diskDriveCount)
    {
        this.diskDriveCount = diskDriveCount;
    }

    @JsonProperty("roles")
    public void setRoles(String roles)
    {
        this.roles = roles;
    }

    @JsonProperty("pps_agents")
    public void setPpsAgents(String ppsAgents)
    {
        this.ppsAgents = ppsAgents;
    }

    @JsonProperty("cage_code")
    public void setCageCode(String cageCode)
    {
        this.cageCode = cageCode;
    }

    @JsonProperty("warranty_info")
    public void setWarrantyInfo(String warrantyInfo)
    {
        this.warrantyInfo = warrantyInfo;
    }

    @JsonProperty("pps_customerid")
     public void setPpsCustomerId(String ppsCustomerId)
    {
        this.ppsCustomerId = ppsCustomerId;
    }

    @JsonProperty("kernel_release")
    public void setKernelRelease(String kernelRelease)
    {
        this.kernelRelease = kernelRelease;
    }

    @JsonProperty("raidvolumes")
    public void setRaidVolumes(String raidVolumes)
    {
        this.raidVolumes = raidVolumes;
    }

    @JsonProperty("raiddrivestatus")
    public void setRaidDriveStatus(String raidDriveStatus)
    {
        this.raidDriveStatus = raidDriveStatus;
    }

    @JsonProperty("fqdn")
    public void setFqdn(String fqdn)
    {
        this.fqdn = fqdn;
    }

    @JsonProperty("drac_macaddress")
    public void setDracMacaddress(String dracMacaddress)
    {
        this.dracMacaddress = dracMacaddress;
    }

    @JsonProperty("changes")
    public void setChanges(String changes)
    {
        this.changes = changes;
    }

    @JsonProperty("file_systems")
    public void setFileSystems(String fileSystems)
    {
        this.fileSystems = fileSystems;
    }

    @JsonProperty("netdriver_version")
    public void setNetdriverVersion(String netdriverVersion)
    {
        this.netdriverVersion = netdriverVersion;
    }

    @JsonProperty("netdriver_firmware")
    public void setNetdriverFirmware(String netdriverFirmware)
    {
        this.netdriverFirmware = netdriverFirmware;
    }

    @JsonProperty("guest_fqdns")
    public void setGuestFqdns(String guestFqdns)
    {
        this.guestFqdns = guestFqdns;
    }

    @JsonProperty("inventory_component_type")
    public void setInventoryComponentType(String inventoryComponentType)
    {
        this.inventoryComponentType = inventoryComponentType;
    }

    @JsonProperty("customers")
    public void setCustomers(String customers)
    {
        this.customers = customers;
    }

    @JsonProperty("agent_type")
    public void setAgentType(String agentType)
    {
        this.agentType = agentType;
    }

    @JsonProperty("svc_id")
    public void setSvcId(String svcId)
    {
        this.svcId = svcId;
    }

    @JsonProperty("pps_config_role")
    public void setPpsConfigRole(String ppsConfigRole)
    {
        this.ppsConfigRole = ppsConfigRole;
    }

    @JsonProperty("host_fqdn")
    public void setHostFqdn(String hostFqdn)
    {
        this.hostFqdn = hostFqdn;
    }

    @JsonProperty("power_supply_watts")
    public void setPowerSupplyWatts(String powerSupplyWatts)
    {
        this.powerSupplyWatts = powerSupplyWatts;
    }

    @JsonProperty("drac")
    public void setDrac(String drac)
    {
        this.drac = drac;
    }

    @JsonProperty("physical_processor_count")
    public void setPhysicalProcessorCount(String physicalProcessorCount)
    {
        this.physicalProcessorCount = physicalProcessorCount;
    }

    @JsonProperty("status")
    public void setStatus(String status)
    {
        this.status = status;
    }

    @JsonProperty("date_created")
    public void setDateCreated(String dateCreated)
    {
        this.dateCreated = dateCreated;
    }

    @JsonProperty("memory_size")
    public void setMemorySize(String memorySize)
    {
        this.memorySize = memorySize;
    }

    @JsonProperty("raidbaddrives")
    public void setRaidBadDrives(String raidBadDrives)
    {
        this.raidBadDrives = raidBadDrives;
    }

    @JsonProperty("netdriver_duplex")
    public void setNetdriverDuplex(String netdriverDuplex)
    {
        this.netdriverDuplex = netdriverDuplex;
    }

    @JsonProperty("mac_address")
    public void setMacAddress(String macAddress)
    {
        this.macAddress = macAddress;
    }

    @JsonProperty("primary_interface")
    public void setPrimaryInterface(String primaryInterface)
    {
        this.primaryInterface = primaryInterface;
    }

    @JsonProperty("interfaces")
    public void setInterfaces(String interfaces)
    {
        this.interfaces = interfaces;
    }

    @JsonProperty("raidcontroller")
    public void setRaidController(String raidController)
    {
        this.raidController = raidController;
    }

    @JsonProperty("processors")
    public void setProcessors(String processors)
    {
        this.processors = processors;
    }

    @JsonProperty("virtual")
    public void setVirtual(String virtual)
    {
        this.virtual = virtual;
    }

    @JsonProperty("netdriver")
    public void setNetdriver(String netdriver)
    {
        this.netdriver = netdriver;
    }

    @JsonProperty("power_supply_count")
    public void setPowerSupplyCount(String powerSupplyCount)
    {
        this.powerSupplyCount = powerSupplyCount;
    }

    @JsonProperty("data_center_code")
    public void setDataCenterCode(String dataCenterCode)
    {
        this.dataCenterCode = dataCenterCode;
    }

    @JsonProperty("manufacturer")
    public void setManufacturer(String manufacturer)
    {
        this.manufacturer = manufacturer;
    }

    @JsonProperty("drac_version")
    public void setDracVersion(String dracVersion)
    {
        this.dracVersion = dracVersion;
    }

    @JsonProperty("notes")
    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    @JsonProperty("netdriver_speed")
    public void setNetdriverSpeed(String netdriverSpeed)
    {
        this.netdriverSpeed = netdriverSpeed;
    }

    @JsonProperty("product_name")
    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    @JsonProperty("ip_address")
    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    @JsonProperty("raiddrives")
    public void setRaidDrives(String raidDrives)
    {
        this.raidDrives = raidDrives;
    }

    @JsonProperty("date_modified")
    public void setDateModified(String dateModified)
    {
        this.dateModified = dateModified;
    }

    @JsonProperty("raidtype")
    public void setRaidType(String raidType)
    {
        this.raidType = raidType;
    }

    @JsonProperty("hardware_class")
    public void setHardwareClass(String hardwareClass)
    {
        this.hardwareClass = hardwareClass;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        System system = (System) o;

        if (fqdn != null ? !fqdn.equals(system.fqdn) : system.fqdn != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return fqdn != null ? fqdn.hashCode() : 0;
    }
}

