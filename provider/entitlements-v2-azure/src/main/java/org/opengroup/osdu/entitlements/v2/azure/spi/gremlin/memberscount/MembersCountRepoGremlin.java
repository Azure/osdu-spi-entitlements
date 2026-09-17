//  Copyright © Microsoft Corporation
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

package org.opengroup.osdu.entitlements.v2.azure.spi.gremlin.memberscount;

import lombok.RequiredArgsConstructor;
import org.opengroup.osdu.entitlements.v2.model.memberscount.MembersCountResponseDto;
import org.opengroup.osdu.entitlements.v2.model.memberscount.MembersCountServiceDto;
import org.opengroup.osdu.entitlements.v2.spi.memberscount.DefaultMembersCountRepo;
import org.opengroup.osdu.entitlements.v2.spi.retrievegroup.RetrieveGroupRepo;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Primary
public class MembersCountRepoGremlin extends DefaultMembersCountRepo {
    private final RetrieveGroupRepo retrieveGroupRepo;

    @Override
    public MembersCountResponseDto getMembersCount(MembersCountServiceDto membersCountServiceDto) {
        return retrieveGroupRepo.getMembersCount(membersCountServiceDto.getPartitionId(), membersCountServiceDto.getGroupId(), membersCountServiceDto.getRole());
    }
}
